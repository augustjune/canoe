package samples

import canoe.api._
import canoe.syntax._
import cats.effect.std.Semaphore
import cats.effect.{IO, IOApp}
import cats.syntax.all._
import fs2.Stream

/** Example of concurrent execution of >1 scenarios.
  *
  * Each scenario is evaluated independently across different chats,
  * without blocking current chat from evaluating other scenarios.
  */
object ConcurrentScenarios extends IOApp.Simple {

  val token: String = "<your telegram token>"

  def run: IO[Unit] =
    Stream
      .resource(TelegramClient[IO](token))
      .flatMap { implicit client =>
        Stream.eval(Semaphore[IO](0)).flatMap { sem =>
          // Both scenarios use shared semaphore
          // to achieve the interaction across different chats.
          Bot.polling[IO].follow(pop(sem), push(sem))
        }
      }
      .compile
      .drain

  def pop[F[_]: TelegramClient](semaphore: Semaphore[F]): Scenario[F, Unit] =
    for {
      m <- Scenario.expect(command("pop"))
      _ <- Scenario.eval(m.chat.send("Waiting for available elements.."))
      _ <- Scenario.eval(semaphore.acquire)
      _ <- Scenario.eval(m.reply("Done."))
    } yield ()

  def push[F[_]: TelegramClient](semaphore: Semaphore[F]): Scenario[F, Unit] =
    for {
      chat <- Scenario.expect(command("push").chat)
      _    <- Scenario.eval(semaphore.release)
      _    <- Scenario.eval(chat.send("Pushed one element."))
    } yield ()
}
