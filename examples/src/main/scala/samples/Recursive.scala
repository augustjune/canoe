package samples

import canoe.api._
import canoe.models.Chat
import canoe.syntax._
import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.functor._
import fs2.Stream

/**
  * Example of stack safe self recursive call of Scenario
  */
object Recursive extends IOApp {

  val token: String = "<your telegram token>"

  def run(args: List[String]): IO[ExitCode] =
    Stream
      .resource(TelegramClient.global[IO](token))
      .flatMap { implicit client =>
        Bot.polling[IO].follow(learnNaturals)
      }
      .compile.drain.as(ExitCode.Success)

  final val FIRST_NATURAL_NUMBER = 0

  def learnNaturals[F[_]: TelegramClient]: Scenario[F, Unit] =
    for {
      chat <- Scenario.start(command("naturals").chat)
      _    <- Scenario.eval(chat.send("Hi. Let's learn what natural numbers are there."))
      _    <- repeat(chat, FIRST_NATURAL_NUMBER)
    } yield ()

  def repeat[F[_]: TelegramClient](chat: Chat, i: Int): Scenario[F, Unit] =
    for {
      _ <- Scenario.eval(chat.send(s"Repeat after me: $i"))
      m <- Scenario.next(text)
      _ <-
        if (m == i.toString) Scenario.eval(chat.send("Well done. Let's go to the next one!")) >> repeat(chat, i + 1)
        else Scenario.eval(chat.send("Not even close. You should try again")) >> repeat(chat, i)
    } yield ()
}
