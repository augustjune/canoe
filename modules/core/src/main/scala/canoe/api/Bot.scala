package canoe.api

import canoe.api.sources.Polling
import canoe.clients.TelegramClient
import canoe.models.Update
import canoe.models.messages.TelegramMessage
import canoe.scenarios.Scenario
import cats.effect.Concurrent
import cats.effect.concurrent.Ref
import cats.implicits._
import fs2.concurrent.Broadcast
import fs2.{Pipe, Pull, Stream}

class Bot[F[_]: Concurrent] private (source: UpdateSource[F]) {

  def updates: Stream[F, Update] = source.updates

  def follow(scenarios: Scenario[F, Unit]*): Stream[F, Update] =
    forkThrough(updates, scenarios.map(pipes.messages[F] andThen runScenario(_)): _*)

  private def forkThrough[A](stream: Stream[F, A], pipes: Pipe[F, A, Unit]*): Stream[F, A] =
    stream.through(Broadcast.through((identity: Pipe[F, A, A]) :: pipes.toList.map(_.andThen(_.drain)): _*))

  private def runScenario(scenario: Scenario[F, Unit])(messages: Stream[F, TelegramMessage]): Stream[F, Nothing] = {

    val filterByFirst: Pipe[F, TelegramMessage, TelegramMessage] =
      _.pull.peek1.flatMap {
        case Some((m, rest)) => rest.filter(_.chat.id == m.chat.id).pull.echo
        case None            => Pull.done
      }.stream

    def go(input: Stream[F, TelegramMessage], ids: Ref[F, Set[Long]]): Pull[F, Nothing, Unit] =
      input.pull.peek1.flatMap {
        case Some((m, rest)) =>
          Pull.eval(ids.get.map(_.contains(m.chat.id))).flatMap {
            case true => // contains
              go(rest.tail, ids)

            case false => // doesn't contain
              Pull.eval(ids.update(_ + m.chat.id)) >>
                go(forkThrough(rest, filterByFirst andThen scenario).tail, ids)
          }

        case None => Pull.done
      }

    Stream.eval(Ref.of(Set.empty[Long])).flatMap(ids => go(messages, ids).stream)
  }
}

object Bot {

  def polling[F[_]](implicit C: Concurrent[F], client: TelegramClient[F]): Bot[F] =
    new Bot[F](new Polling[F](client))
}
