package canoe.models

import io.circe.Encoder
import io.circe.syntax._

/**
  * Represents the id of a chat.
  * This may be the numeric id or channel username in the format `@channelusername`.
  */
sealed trait ChatId {
  def isChannel: Boolean
  def isChat: Boolean = !isChannel
}

object ChatId {
  implicit def fromChat[T](id: Long): ChatId = ChatId(id)
  implicit def fromChannel[T](username: String): ChatId = ChatId(username)

  final case class Chat(id: Long) extends ChatId {
    override def isChannel: Boolean = false
  }

  final case class Channel(id: String) extends ChatId {
    override def isChannel: Boolean = true
  }

  def apply(id: Long): ChatId = Chat(id)
  def apply(username: String): ChatId = Channel(username)

  implicit val chatIdEncoder: Encoder[ChatId] = Encoder.instance {
    case ChatId.Chat(chat)       => chat.asJson
    case ChatId.Channel(channel) => channel.asJson
  }
}
