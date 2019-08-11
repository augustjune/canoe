package com.canoe.telegram.api

import cats.effect.Concurrent
import cats.effect.concurrent.Ref
import cats.implicits._
import com.canoe.telegram.clients.RequestHandler
import com.canoe.telegram.methods.updates.GetUpdates
import com.canoe.telegram.models._
import com.canoe.telegram.models.messages.TelegramMessage
import com.canoe.telegram.scenarios.Scenario
import fs2.concurrent.Broadcast
import fs2.{Pipe, Pull, Stream}

class Bot[F[_]: Concurrent](client: RequestHandler[F]) {

  def updates: Stream[F, Update] = pollUpdates(0)

  def follow(scenarios: List[Scenario[F, Unit]]): Stream[F, Update] =
    forkThrough(updates, scenarios.map(runScenario(_) _ compose pipes.incomingMessages[F]) :_*)

  private def forkThrough[A](stream: Stream[F, A], pipes: Pipe[F, A, Any]*): Stream[F, A] =
    stream.through(Broadcast.through((identity: Pipe[F, A, A]) :: pipes.toList.map(_.andThen(_.drain)): _*))

  private def runScenario[A](scenario: Scenario[F, A])(messages: Stream[F, TelegramMessage]): Stream[F, A] = {

    val filterByFirst: Pipe[F, TelegramMessage, TelegramMessage] =
      _.pull.peek1.flatMap {
        case Some((m, rest)) => rest.filter(_.chat.id == m.chat.id).pull.echo
        case None => Pull.done
      }.stream

    def go(input: Stream[F, TelegramMessage], ids: Ref[F, Set[Long]]): Pull[F, A, Unit] =
      input.pull.peek1.flatMap {
        case Some((m, rest)) =>
          Pull.eval(ids.get.map(_.contains(m.chat.id))).flatMap {
            case true => // contains
              go(rest.tail, ids)

            case false => // doesn't contain
              Pull.eval(ids.update(_ + m.chat.id)) >>
                go(forkThrough(rest, scenario compose filterByFirst).tail, ids)
          }

        case None => Pull.done
      }

    Stream.eval(Ref.of(Set.empty[Long])).flatMap(ids => go(messages, ids).stream)
  }

  private def pollUpdates(startOffset: Long): Stream[F, Update] =
    Stream(()).repeat.covary[F]
      .evalMapAccumulate(startOffset) { case (offset, _) => requestUpdates(offset) }
      .flatMap { case (_, updates) => Stream.emits(updates) }

  private def requestUpdates(offset: Long): F[(Long, List[Update])] =
    client
      .execute(GetUpdates(Some(offset)))
      .map(_.toList)
      .map(updates => (lastId(updates).map(_ + 1).getOrElse(offset), updates))


  private def lastId(updates: List[Update]): Option[Long] =
    updates match {
      case Nil => None
      case nonEmpty => Some(nonEmpty.map(_.updateId).max)
    }
}
