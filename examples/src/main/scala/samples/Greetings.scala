package samples

import canoe.api._
import canoe.syntax._
import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.functor._

/**
  * Example of interaction between a user and the bot
  */
object Greetings extends IOApp {

  val token: String = "<your telegram token>"

  def run(args: List[String]): IO[ExitCode] =
    TelegramClient
      .global[IO](token)
      .use { implicit client =>
        Bot.polling[IO].follow(greetings).compile.drain
      }
      .as(ExitCode.Success)

  def greetings[F[_]: TelegramClient]: Scenario[F, Unit] =
    for {
      chat  <- Scenario.start(command("hi").chat)
      _     <- Scenario.eval(chat.send("Hello. What's your name?"))
      name  <- Scenario.next(text)
      _     <- Scenario.eval(chat.send(s"Nice to meet you, $name"))
    } yield ()
}
