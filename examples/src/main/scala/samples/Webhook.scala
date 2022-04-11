package samples

import canoe.api._
import canoe.syntax._
import cats.effect.{IO, IOApp}
import cats.syntax.all._
import fs2.Stream

/** Example of defining a bot using webhook.
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
object Webhook extends IOApp.Simple {

  /** URL to which Telegram updates will be sent.
    * This address must be reachable for the Telegram, so in case you're using local environment
    * you'd have to expose your local host to the Internet.
    * It can be achieved using ngrok simply following
    * this [[https://developer.github.com/webhooks/configuring/#using-ngrok comprehensive guide]].
    */
  val url: String = "<your server url>"

  val token: String = "<your telegram token>"

  def run: IO[Unit] =
    Stream
      .resource(TelegramClient[IO](token))
      .flatMap { implicit client =>
        Stream.resource(Bot.hook[IO](url)).flatMap(_.follow(greetings))
      }
      .compile
      .drain

  def greetings[F[_]: TelegramClient]: Scenario[F, Unit] =
    for {
      chat <- Scenario.expect(command("hi").chat)
      _    <- Scenario.eval(chat.send("Hello. What's your name?"))
      name <- Scenario.expect(text)
      _    <- Scenario.eval(chat.send(s"Nice to meet you, $name"))
    } yield ()
}
