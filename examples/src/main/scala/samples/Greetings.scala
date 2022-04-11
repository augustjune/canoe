package samples

import canoe.api._
import canoe.syntax._
import cats.effect.{IO, IOApp}
import cats.syntax.functor._
import fs2.Stream

/** Basic example of interaction between a user and the bot.
  */
object Greetings extends IOApp.Simple {

  val token: String = "<your telegram token>"

  def run: IO[Unit] =
    Stream
      .resource(TelegramClient[IO](token))
      .flatMap(implicit client => Bot.polling[IO].follow(greetings))
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
