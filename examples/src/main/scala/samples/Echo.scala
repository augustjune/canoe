package samples

import canoe.api._
import canoe.models.messages.{AnimationMessage, StickerMessage, TelegramMessage, TextMessage}
import canoe.syntax._
import cats.effect.{IO, IOApp}
import cats.syntax.functor._
import fs2.Stream

/** Example of echos bot that will answer to you with the message you've sent to him
  */
object Echo extends IOApp.Simple {
  val token: String = "<your telegram token>"

  def run: IO[Unit] =
    Stream
      .resource(TelegramClient.global[IO](token))
      .flatMap(implicit client => Bot.polling[IO].follow(echos))
      .compile
      .drain

  def echos[F[_]: TelegramClient]: Scenario[F, Unit] =
    for {
      msg <- Scenario.expect(any)
      _   <- Scenario.eval(echoBack(msg))
    } yield ()

  def echoBack[F[_]: TelegramClient](msg: TelegramMessage): F[_] = msg match {
    case textMessage: TextMessage           => msg.chat.send(textMessage.text)
    case animationMessage: AnimationMessage => msg.chat.send(animationMessage.animation)
    case stickerMessage: StickerMessage     => msg.chat.send(stickerMessage.sticker)
    case _                                  => msg.chat.send("Sorry! I can't echo that back.")
  }
}
