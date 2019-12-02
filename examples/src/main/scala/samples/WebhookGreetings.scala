package samples

import canoe.api._
import canoe.syntax._
import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.all._
import fs2.Stream

/**
  * Example of defining a bot using webhook.
  *
  * The webhook is released when you exit the application or it encounters error or cancellation.
  * It is important to always delete the webhook afterwards,
  * since it blocks you from getting updates using polling method.
  *
  * In case there's installed webhook for your bot and you try to use [[canoe.methods.updates.GetUpdates]] method,
  * Telegram service replies with `409 Conflict` response.
  *
  * The webhook can also be deleted manually using [[canoe.methods.webhooks.DeleteWebhook]] method.
  */
object WebhookGreetings extends IOApp {

  val token: String = "<your telegram token>"
  val url: String = "<your server url>"

  def run(args: List[String]): IO[ExitCode] =
    Stream
      .resource(TelegramClient.global[IO](token))
      .flatMap { implicit client =>
        Stream.resource(Bot.hook[IO](url)).flatMap(_.follow(greetings))
      }
      .compile.drain.as(ExitCode.Success)

  def greetings[F[_]: TelegramClient]: Scenario[F, Unit] =
    for {
      chat <- Scenario.expect(command("hi").chat)
      _    <- Scenario.eval(chat.send("Hello. What's your name?"))
      name <- Scenario.expect(text)
      _    <- Scenario.eval(chat.send(s"Nice to meet you, $name"))
    } yield ()
}
