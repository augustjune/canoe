package samples

import canoe.api._
import canoe.models.Chat
import canoe.syntax._
import cats.Applicative
import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.functor._
import fs2.Stream

/**
  * Example using compositional property of scenarios
  * by combining them into more complex registration process
  */
object Composition extends IOApp {

  val token: String = "<your telegram token>"

  trait Service[F[_]] {
    def userExists(username: String): F[Boolean]

    def register(username: String, password: String): F[User]
  }

  trait User {
    def name: String
  }

  val service: Service[IO] = ???

  def run(args: List[String]): IO[ExitCode] =
    Stream
      .resource(TelegramClient.global[IO](token))
      .flatMap { implicit client =>
        Bot.polling[IO].follow(signup(service))
      }
      .compile.drain.as(ExitCode.Success)

  def signup[F[_]: Applicative: TelegramClient](service: Service[F]): Scenario[F, Unit] =
    for {
      chat <- Scenario.start(command("signup").chat)
      user <- registerUser(chat, service).cancelWhen(command("cancel"))
      _    <- Scenario.eval(chat.send(s"Registration completed. Welcome, ${user.name}"))
    } yield ()

  def registerUser[F[_]: Applicative: TelegramClient](chat: Chat, service: Service[F]): Scenario[F, User] =
    for {
      name <- provideUsername(chat, service)
      pass <- providePass(chat)
      user <- Scenario.eval(service.register(name, pass))
    } yield user

  def provideUsername[F[_]: Applicative: TelegramClient](chat: Chat, service: Service[F]): Scenario[F, String] =
    for {
      _      <- Scenario.eval(chat.send("Enter your nickname"))
      nick   <- Scenario.next(text)
      exists <- Scenario.eval(service.userExists(nick))
      res    <-
        if (exists)
          Scenario.eval(chat.send("User with such nick already exists. Please try another one")) >>
            provideUsername(chat, service)
        else Scenario.pure(nick)
    } yield res

  def providePass[F[_]: TelegramClient](chat: Chat): Scenario[F, String] =
    for {
      _         <- Scenario.eval(chat.send("Enter your password (after you send the message it will be deleted)"))
      pass      <- enterPass(chat)
      _         <- Scenario.eval(chat.send("Repeat your password"))
      reentered <- enterPass(chat)
      _         <-
        if (pass == reentered) Scenario.eval(chat.send("Your password is stored."))
        else Scenario.eval(chat.send("Provided passwords don't match. Try again")) >> providePass(chat)
    } yield pass

  def enterPass[F[_]: TelegramClient](chat: Chat): Scenario[F, String] =
    for {
      passwordMessage <- Scenario.next(textMessage)
      _               <- Scenario.eval(passwordMessage.delete)
    } yield passwordMessage.text

}
