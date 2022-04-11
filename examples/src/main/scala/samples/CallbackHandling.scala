package samples

import canoe.api._
import canoe.api.models.Keyboard
import canoe.models.{CallbackButtonSelected, InlineKeyboardButton, InlineKeyboardMarkup, Update}
import canoe.models.messages.{AnimationMessage, StickerMessage, TelegramMessage, TextMessage}
import canoe.syntax._
import cats.{Applicative, Monad}
import cats.effect.{IO, IOApp}
import cats.syntax.all._
import fs2.{Pipe, Stream}

/** Example of echos bot that will answer to you with
  * the callback data [[canoe.models.InlineKeyboardButton.callbackData]]
  * has been sent to him
  */
object CallbackHandling extends IOApp.Simple {
  val token: String = "<your telegram token>"

  def run: IO[Unit] =
    Stream
      .resource(TelegramClient[IO](token))
      .flatMap { implicit client =>
        Bot
          .polling[IO]
          .follow(echos)
          .through(answerCallbacks)
      }
      .compile
      .drain

  val inlineBtn = InlineKeyboardButton.callbackData(text = "button", cbd = "callback data")

  val inlineKeyboardMarkUp = InlineKeyboardMarkup.singleButton(inlineBtn)
  val keyboardMarkUp = Keyboard.Inline(inlineKeyboardMarkUp)

  def echos[F[_]: TelegramClient]: Scenario[F, Unit] =
    for {
      msg <- Scenario.expect(command("callback"))
      _   <- Scenario.eval(msg.chat.send(content = "pretty message", keyboard = keyboardMarkUp))
    } yield ()

  def answerCallbacks[F[_]: Monad: TelegramClient]: Pipe[F, Update, Update] =
    _.evalTap {
      case CallbackButtonSelected(_, query) =>
        query.data match {
          case Some(cbd) =>
            for {
              _ <- query.message.traverse(_.chat.send(cbd))
              _ <- query.finish
            } yield ()
          case _ => Applicative[F].unit
        }
      case _ => Applicative[F].unit
    }
}
