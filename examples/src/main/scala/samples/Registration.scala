package samples

import canoe.api._
import canoe.models.Chat
import canoe.syntax._
import cats.effect.{IO, IOApp}
import cats.syntax.functor._
import fs2.Stream

/** Example using compositional property of scenarios
  * by combining them into more complex registration process
  */
object Registration extends IOApp.Simple {

  val token: String = "<your telegram token>"

  trait Service[F[_]] {
    def userExists(username: String): F[Boolean]

    def register(username: String, password: String): F[User]
  }

  trait User {
    def name: String
  }

  val service: Service[IO] = ???

  def run: IO[Unit] =
    Stream
      .resource(TelegramClient.global[IO](token))
      .flatMap { implicit client =>
        Bot.polling[IO].follow(signup(service))
      }
      .compile
      .drain

  def signup[F[_]: TelegramClient](service: Service[F]): Scenario[F, Unit] =
    for {
      chat <- Scenario.expect(command("signup").chat)
      user <- registerUser(chat, service).stopOn(command("cancel").isDefinedAt)
      _    <- Scenario.eval(chat.send(s"Registration completed. Welcome, ${user.name}"))
    } yield ()

  def registerUser[F[_]: TelegramClient](chat: Chat, service: Service[F]): Scenario[F, User] =
    for {
      name <- provideUsername(chat, service)
      pass <- providePass(chat)
      user <- Scenario.eval(service.register(name, pass))
    } yield user

  def provideUsername[F[_]: TelegramClient](chat: Chat, service: Service[F]): Scenario[F, String] =
    for {
      _      <- Scenario.eval(chat.send("Enter your nickname"))
      nick   <- Scenario.expect(text)
      exists <- Scenario.eval(service.userExists(nick))
      res <-
        if (exists)
          Scenario.eval(chat.send("User with such nick already exists. Please try another one")) >>
            provideUsername(chat, service)
        else Scenario.pure[F](nick)
    } yield res

  def providePass[F[_]: TelegramClient](chat: Chat): Scenario[F, String] =
    for {
      _         <- Scenario.eval(chat.send("Enter your password (after you send the message it will be deleted)"))
      pass      <- enterPass(chat)
      _         <- Scenario.eval(chat.send("Repeat your password"))
      reentered <- enterPass(chat)
      r <-
        if (pass == reentered) Scenario.eval(chat.send("Your password is stored.")).as(pass)
        else Scenario.eval(chat.send("Provided passwords don't match. Try again")) >> providePass(chat)
    } yield r

  def enterPass[F[_]: TelegramClient](chat: Chat): Scenario[F, String] =
    for {
      passwordMessage <- Scenario.expect(textMessage)
      _               <- Scenario.eval(passwordMessage.delete)
    } yield passwordMessage.text

}
