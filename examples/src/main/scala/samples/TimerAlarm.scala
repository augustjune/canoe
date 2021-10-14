package samples

import canoe.api._
import canoe.syntax._
import canoe.models.messages.TextMessage
import cats.effect.{IO, IOApp, Temporal}
import fs2.Stream

import scala.util.Try
import scala.concurrent.duration._

/** Example of scheduling a job after user-defined delay.
  * Each user can schedule any number of jobs and all of them
  * will be executed concurrently without blocking each other.
  */
object TimerAlarm extends IOApp.Simple {
  val token: String = "<your telegram token>"

  def run: IO[Unit] =
    Stream
      .resource(TelegramClient.global[IO](token))
      .flatMap(implicit client => Bot.polling[IO].follow(alarm))
      .compile
      .drain

  def alarm[F[_]: TelegramClient: Temporal]: Scenario[F, Unit] =
    for {
      chat <- Scenario.expect(command("alarm").chat)
      _    <- Scenario.eval(chat.send("Tell me in how many seconds you want to be notified?"))
      in   <- Scenario.expect(textMessage)
      sec = Try(in.text.toInt).toOption.filter(_ > 0)
      _ <- sec match {
        case Some(i) => setTimer(in, i)
        case None    => Scenario.eval(in.reply("I'm sorry, but I couldn't get that (expecting positive number)"))
      }
    } yield ()

  def setTimer[F[_]: TelegramClient: Temporal](m: TextMessage, i: Int): Scenario[F, Unit] =
    for {
      _ <- Scenario.eval(m.chat.send(s"Timer is set. You will receive a reply after $i seconds"))
      _ <- Scenario.eval(Temporal[F].sleep(i.seconds))
      _ <- Scenario.eval(m.reply("Time's up."))
    } yield ()
}
