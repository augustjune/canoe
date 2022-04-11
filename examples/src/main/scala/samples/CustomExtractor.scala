package samples

import canoe.api._
import canoe.models.messages.UserMessage
import canoe.syntax._
import cats.effect.{IO, IOApp}
import cats.syntax.functor._
import fs2.Stream

object CustomExtractor extends IOApp.Simple {
  val token: String = "<your telegram token>"
  val userIdToGreet: Int = -1

  def run: IO[Unit] =
    Stream
      .resource(TelegramClient[IO](token))
      .flatMap { implicit client =>
        Bot.polling[IO].follow(greetParticularUser(userIdToGreet))
      }
      .compile
      .drain

  def greetParticularUser[F[_]: TelegramClient](userId: Long): Scenario[F, Unit] =
    for {
      msg <- Scenario.expect(particularUsersMessages(userId))
      _   <- Scenario.eval(msg.chat.send(s"I was waiting for you ${name(msg)}"))
    } yield ()

  def particularUsersMessages(userId: Long): Expect[UserMessage] = {
    case m: UserMessage if m.from.map(_.id).contains(userId) => m
  }

  def name(msg: UserMessage): String = msg.from.map(_.firstName).getOrElse("unknown")
}
