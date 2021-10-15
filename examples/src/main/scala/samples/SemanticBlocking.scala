package samples

import canoe.api._
import canoe.models.Chat
import canoe.syntax._
import cats.effect.{IO, IOApp, Temporal}
import cats.syntax.functor._
import fs2.Stream

import scala.concurrent.duration._
import scala.util.Try

/** Example of execution semantic blocking within a scenario.
  */
object SemanticBlocking extends IOApp.Simple {

  val token: String = "<your telegram token>"

  def run: IO[Unit] =
    Stream
      .resource(TelegramClient.global[IO](token))
      .flatMap { implicit client =>
        Bot.polling[IO].follow(count)
      }
      .compile
      .drain

  def count[F[_]: TelegramClient: Temporal]: Scenario[F, Unit] =
    for {
      m <- Scenario.expect(command("count"))
      start = Try(m.text.split(" ")(1).toInt).getOrElse(10)
      _ <- repeat(m.chat, start)
    } yield ()

  def repeat[F[_]: TelegramClient: Temporal](chat: Chat, i: Int): Scenario[F, Unit] =
    if (i <= 0) Scenario.eval(chat.send("Done.")).void
    else
      for {
        _ <- Scenario.eval(chat.send(s"$i.."))
        _ <- Scenario.eval(Temporal[F].sleep(1.second))
        _ <- repeat(chat, i - 1)
      } yield ()
}
