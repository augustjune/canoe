package canoe.api

import canoe.api.sources.{Hook, Polling}
import canoe.models.messages.TelegramMessage
import canoe.models.{InputFile, Update}
import cats.effect.concurrent.{Deferred, Ref}
import cats.effect.{Concurrent, ConcurrentEffect, Resource, Timer}
import cats.instances.option._
import cats.syntax.all._
import fs2.concurrent.Topic
import fs2.{Pipe, Stream}

import scala.concurrent.duration.FiniteDuration

/**
  * An instance which can communicate with Telegram service and
  * interact with other Telegram users in a certain predefined way
  */
class Bot[F[_]: Concurrent: Timer] private[api] (val updates: Stream[F, Update]) {

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
    * which means that scenario cannot be blocked by any other scenario being in progress.
    *
    * All the behavior is suspended as an effect of resulting stream, without changing its elements.
    * Also, result stream is not halted by the execution of any particular scenario.
    *
    * @return Stream of all updates which your bot receives from Telegram service
    */
  def follow(scenarios: Scenario[F, Unit]*): Stream[F, Update] = {
    def runScenarios(updates: Topic[F, Update]): Stream[F, Nothing] =
      updates
        .subscribe(1)
        .through(pipes.messages)
        .map(m => Stream.emits(scenarios).map(sc => fork(updates, m).through(sc.pipe)).parJoinUnbounded.drain)
        .parJoinUnbounded

    def fork(updates: Topic[F, Update], m: TelegramMessage): Stream[F, TelegramMessage] =
      updates
        .subscribe(1)
        .through(filterMessages(m.chat.id))
        .through(debounce)
        .cons1(m)

    def filterMessages(id: Long): Pipe[F, Update, TelegramMessage] =
      _.through(pipes.messages).filter(_.chat.id == id)

    def debounce[F[_]: Concurrent, A]: Pipe[F, A, A] =
      input =>
        Stream.eval(Ref[F].of[Option[Deferred[F, A]]](None)).flatMap { ref =>
          val hook = Stream
            .repeatEval(Deferred[F, A])
            .evalMap(df => ref.set(Some(df)) *> df.get)

          val update = input.evalMap { a =>
            ref.getAndSet(None).flatMap(_.traverse_(_.complete(a)))
          }

          hook.concurrently(update)
        }

    Stream.eval(Broadcast[F, Update]).flatMap { topic =>
      val pop = updates.evalTap(topic.publish1)
      val run = runScenarios(topic)
      pop.concurrently(run)
    }
  }
}

object Bot {

  /**
    * Creates a bot which operates on provided updates.
    */
  def fromStream[F[_]: Concurrent: Timer](updates: Stream[F, Update]): Bot[F] = new Bot(updates)

  /**
    * Creates a bot which receives incoming updates using long polling mechanism.
    *
    * See [[https://en.wikipedia.org/wiki/Push_technology#Long_polling wiki]].
    */
  def polling[F[_]: Concurrent: Timer: TelegramClient]: Bot[F] =
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
    * @param host        Network interface to bind the server
    * @param port        Port which will be used for listening for the incoming updates.
    *                    Default is 8443.
    * @param certificate Public key of self-signed certificate (including BEGIN and END portions)
    */
  def hook[F[_]: TelegramClient: ConcurrentEffect: Timer](
    url: String,
    host: String = "0.0.0.0",
    port: Int = 8443,
    certificate: Option[InputFile] = None
  ): Resource[F, Bot[F]] =
    Hook.install(url, host, port, certificate).map(h => new Bot(h.updates))
}
