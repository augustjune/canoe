package samples

import canoe.api._
import canoe.models.messages.{AnimationMessage, StickerMessage, TelegramMessage, TextMessage}
import canoe.syntax._
import cats.syntax.functor._
import cats.Functor
import cats.effect.{IO, IOApp}
import fs2.Stream

/** Example of echos bot that will answer to you with the message you've sent to him
  */
object Echo extends IOApp.Simple {
  val token: String = "<your telegram token>"

  def run: IO[Unit] =
    Stream
      .resource(TelegramClient[IO](token))
      .flatMap(implicit client => Bot.polling[IO].follow(echos))
      .compile
      .drain

  def echos[F[_]: TelegramClient: Functor]: Scenario[F, Unit] =
    for {
      msg <- Scenario.expect(any)
      _   <- Scenario.eval(echoBack[F](msg))
    } yield ()

  def echoBack[F[_]: TelegramClient: Functor](msg: TelegramMessage): F[Unit] = msg match {
    case textMessage: TextMessage           => msg.chat.send(textMessage.text).void
    case animationMessage: AnimationMessage => msg.chat.send(animationMessage.animation).void
    case stickerMessage: StickerMessage     => msg.chat.send(stickerMessage.sticker).void
    case _                                  => msg.chat.send("Sorry! I can't echo that back.").void
  }
}
