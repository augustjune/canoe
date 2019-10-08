package canoe.api.models

import canoe.api.TelegramClient
import canoe.methods.messages.StopPoll
import canoe.models.messages.PollMessage
import canoe.models.{InlineKeyboardMarkup, Poll}
import canoe.syntax.methodOps

/**
  * Telegram API for the poll message.
  * Offers a convenient access to the related Telegram methods in OO style.
  *
  * It is a conscious decision to provide this API via extension methods.
  */
final class PollMessageApi(private val pollMessage: PollMessage) extends AnyVal {

  /**
    * Stops the poll sent by the bot.
    */
  def stopPoll[F[_]: TelegramClient](markup: Option[InlineKeyboardMarkup] = None): F[Poll] =
    StopPoll(pollMessage.chat.id, pollMessage.messageId, markup).call

}
