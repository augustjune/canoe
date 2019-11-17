package canoe.api

import canoe.api.sources.{Hook, Polling}
import canoe.models.messages.TelegramMessage
import canoe.models.{InputFile, Update}
import cats.effect.concurrent.Ref
import cats.effect.{Concurrent, ConcurrentEffect, Resource, Timer}
import cats.implicits._
import fs2.concurrent.{Queue, Topic}
import fs2.{Pipe, Stream}

import scala.concurrent.duration.FiniteDuration

/**
  * An instance which can communicate with Telegram service and
  * interact with other Telegram users in a certain predefined way
  */
class Bot[F[_]: Concurrent] private[api] (source: UpdateSource[F]) {

  /**
    * Stream of all updates which your bot receives from Telegram service
    */
  def updates: Stream[F, Update] = source.updates

  /**
    * Defines the behavior of the bot.
    *
    * Bot is reacting to the incoming messages following provided scenarios.
    * When the user input is not matching/stops matching particular scenario
    * it means that current interaction is not described with this scenario
    * and bot will not continue acting it out.
    *
    * @example {{{
    *   val scenario = for {
    *     chat <- Scenario.start(command("first").chat)
    *     _    <- Scenario.eval(chat.send("first message received"))
    *     _    <- Scenario.next(command("second"))
    *     _    <- Scenario.eval(chat.send("second message received"))
    *   }
    *
    *  user > /first
    *  bot > first message received
    *  user > something else
    *  *end of the scenario*
    *
    *  user > /first
    *  bot > first message received
    *  user > /second
    *  bot > second message received
    *  *end of the scenario*
    * }}}
    *
    * Each scenario is handled concurrently across all chats,
    * which means that scenario is blocked only if it's already in progress within the same chat.
    *
    * All the behavior is suspended as an effect of resulting stream, without changing its elements.
    * Also, result stream is not halted by the execution of any particular scenario.
    *
    * @return Stream of all updates which your bot receives from Telegram service
    */
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
                  topic: Topic[F, Update]): Stream[F, Nothing] =
      topic
        .subscribe(1)
        .through(pipes.messages)
        .map { m =>
          Stream
            .eval(register(idsRef, m.chat.id))
            .flatMap { existed =>
              if (existed) Stream.empty
              else
                //  Using queues in order to avoid blocking topic publisher
                Stream.eval(Queue.unbounded[F, TelegramMessage]).flatMap { queue =>
                  val enq = topic
                    .subscribe(1)
                    .through(pipes.messages andThen filterById(m.chat.id))
                    .through(queue.enqueue)

                  val deq = queue.dequeue.through(scenario.pipe).drain

                  deq.concurrently(enq)
                }
            }
        }
        .parJoinUnbounded

    def runAll(scenarios: List[Scenario[F, Unit]],
               updates: Stream[F, Update],
               topic: Topic[F, Update]): Stream[F, Update] = {

      val run = Stream
        .emits(scenarios)
        .zipWith(Stream.repeatEval(Ref[F].of(Set.empty[Long]))) {
          case (scenario, ids) => runSingle(scenario, ids, topic)
        }
        .parJoinUnbounded

      val populate = updates.evalTap(u => topic.publish1(u))

      populate.concurrently(run)
    }

    Stream.eval(Topic[F, Update](Update.Unknown(-1L))).flatMap { topic =>
      runAll(scenarios.toList, updates, topic)
    }
  }
}

object Bot {

  /**
    * Creates a bot which receives incoming updates using long polling mechanism.
    *
    * See [[https://en.wikipedia.org/wiki/Push_technology#Long_polling wiki]].
    */
  def polling[F[_]: Concurrent: TelegramClient]: Bot[F] =
    new Bot[F](Polling.continual)

  /**
    * Creates a bot which receives incoming updates using long polling mechanism
    * with custom polling interval.
    *
    * See [[https://en.wikipedia.org/wiki/Push_technology#Long_polling wiki]].
    */
  def polling[F[_]: Concurrent: Timer: TelegramClient](interval: FiniteDuration): Bot[F] =
    new Bot[F](Polling.metered(interval))

  /**
    * Creates a bot which receives incoming updates by setting a webhook.
    * After the bot is used, the webhook is deleted even in case of interruptions or errors.
    *
    * @param url         HTTPS url to which updates will be sent
    * @param port        Port which will be used for listening for the incoming updates.
    *                    Default is 8443.
    * @param certificate Public key of self-signed certificate (including BEGIN and END portions)
    */
  def hook[F[_]: TelegramClient: ConcurrentEffect: Timer](
    url: String,
    port: Int = 8443,
    certificate: Option[InputFile] = None
  ): Resource[F, Bot[F]] =
    Hook.install(url, port, certificate).map(new Bot(_))
}
