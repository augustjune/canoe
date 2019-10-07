package canoe.api.models

import canoe.api.TelegramClient
import canoe.methods.messages.StopPoll
import canoe.models.messages.PollMessage
import canoe.models.{InlineKeyboardMarkup, Poll}
import canoe.syntax.methodOps

final class PollMessageApi[F[_]: TelegramClient](pollMessage: PollMessage) {

  /**
    * Stops the poll sent by the bot.
    */
  def stopPoll(markup: Option[InlineKeyboardMarkup] = None): F[Poll] =
    StopPoll(pollMessage.chat.id, pollMessage.messageId, markup).call

}
