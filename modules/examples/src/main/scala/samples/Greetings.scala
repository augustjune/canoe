package samples

import canoe.api._
import canoe.api.syntax._
import canoe.clients.TelegramClient
import canoe.models.messages.TextMessage
import canoe.scenarios.Scenario
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
      chat  <- Scenario.start { case m: TextMessage if m.text.startsWith("/hi") => m.chat }
      _     <- Scenario.eval { chat.send("Hello. What's your name?") }
      name  <- Scenario.next { case m: TextMessage => m.text }
      _     <- Scenario.eval { chat.send(s"Nice to meet you, $name") }
    } yield ()
}
