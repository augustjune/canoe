package samples

import canoe.api._
import canoe.api.syntax._
import canoe.clients.TelegramClient
import canoe.models.Chat
import canoe.models.messages.TextMessage
import canoe.scenarios.Scenario
import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.functor._

/**
  * Example of stack safe self recursive call of Scenario
  */
object Recursive extends IOApp {

  val token: String = "<your telegram token>"

  def run(args: List[String]): IO[ExitCode] =
    TelegramClient
      .global[IO](token)
      .use { implicit client =>
        Bot.polling[IO].follow(learnNaturals).compile.drain
      }
      .as(ExitCode.Success)

  final val FIRST_NATURAL_NUMBER = 0

  def learnNaturals[F[_]: TelegramClient]: Scenario[F, Unit] =
    for {
      chat  <- Scenario.start { case m: TextMessage if m.text.startsWith("/repeat") => m.chat }
      _     <- Scenario.eval(chat.send("Hi. Let's learn what natural numbers are there."))
      _     <- repeat(chat, FIRST_NATURAL_NUMBER)
    } yield ()

  def repeat[F[_]: TelegramClient](chat: Chat, i: Int): Scenario[F, Unit] =
    for {
      _ <- Scenario.eval(chat.send(s"Repeat after me: $i"))
      m <- Scenario.next { case m: TextMessage => m.text }
      _ <-
        if (m == i.toString) Scenario.eval(chat.send("Well done. Let's go to the next one!")) >> repeat(chat, i + 1)
        else Scenario.eval(chat.send("Not even close. You should try again")) >> repeat(chat, i)
    } yield ()
}
