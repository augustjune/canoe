package canoe.models.messages

import canoe.models.{Chat, User}

trait UserMessage extends TelegramMessage {

  def from: Option[User]

  def forwardFrom: Option[User]

  def forwardFromChat: Option[Chat]

  def forwardFromMessageId: Option[Int]

  def forwardSignature: Option[String]

  def forwardSenderName: Option[String]

  def forwardDate: Option[Int]

  def replyToMessage: Option[TelegramMessage]

  def editDate: Option[Int]

  def authorSignature: Option[String]
}
