package canoe.api

import canoe.api.sources.Polling
import canoe.models.Update
import canoe.models.messages.TelegramMessage
import cats.effect.Concurrent
import cats.effect.concurrent.Ref
import cats.implicits._
import fs2.concurrent.{Queue, Topic}
import fs2.{Pipe, Stream}

class Bot[F[_]] private (source: UpdateSource[F])(implicit F: Concurrent[F]) {

  def updates: Stream[F, Update] = source.updates

  def follow(scenarios: Scenario[F, Unit]*): Stream[F, Update] = {

    def filterById(id: Long): Pipe[F, TelegramMessage, TelegramMessage] =
      _.filter(_.chat.id == id)

    def register(idsRef: Ref[F, Set[Long]], id: Long): F[Boolean] =
      idsRef.modify { ids =>
        val was = ids.contains(id)
        ids + id -> was
      }

    def runSingle(scenario: Scenario[F, Unit],
                  idsRef: Ref[F, Set[Long]],
                  topic: Topic[F, Option[Update]]): Stream[F, Nothing] =
      topic
        .subscribe(1)
        .unNone
        .through(pipes.messages)
        .map { m =>
          Stream
            .eval(register(idsRef, m.chat.id))
            .flatMap {
              case true  => Stream.empty
              case false =>
                //  Using queue in order to avoid blocking topic publisher
                Stream.eval(Queue.unbounded[F, TelegramMessage]).flatMap { queue =>
                  val enq = topic
                    .subscribe(1)
                    .unNone
                    .through(pipes.messages andThen filterById(m.chat.id))
                    .through(queue.enqueue)

                  val deq = queue.dequeue.through(scenario).drain

                  deq.concurrently(enq)
                }
            }
        }
        .parJoinUnbounded

    def runAll(scenarios: List[Scenario[F, Unit]],
               updates: Stream[F, Update],
               topic: Topic[F, Option[Update]]): Stream[F, Update] = {

      val run = Stream
        .emits(scenarios)
        .zipWith(Stream.repeatEval(Ref[F].of(Set.empty[Long]))) {
          case (scenario, ids) => runSingle(scenario, ids, topic)
        }
        .parJoinUnbounded

      val populate = updates.evalTap(u => topic.publish1(Some(u)))

      populate.concurrently(run)
    }

    Stream.eval(Topic[F, Option[Update]](None)).flatMap { topic =>
      runAll(scenarios.toList, updates, topic)
    }
  }
}

object Bot {
  def polling[F[_]](implicit F: Concurrent[F], client: TelegramClient[F]): Bot[F] =
    new Bot[F](new Polling[F](client))
}
