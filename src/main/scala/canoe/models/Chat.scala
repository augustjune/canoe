package canoe.models

import canoe.models.ChatType.ChatType
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

sealed trait Chat {
  def id: Long
}

object Chat {

  implicit val chatDecoder: Decoder[Chat] = Decoder.instance[Chat] {
    cursor => cursor.get[ChatType]("type").map {
      case ChatType.Private => deriveDecoder[PrivateChat]
      case ChatType.Group => deriveDecoder[GroupChat]
      case ChatType.Supergroup => deriveDecoder[Supergroup]
      case ChatType.Channel => deriveDecoder[Channel]
    }.flatMap(_.tryDecode(cursor))
  }
}

case class PrivateChat(id: Long,
                       username: Option[String],
                       firstName: Option[String],
                       lastName: Option[String]) extends Chat

case class GroupChat(id: Long,
                     title: String,
                     allMembersAreAdministrators: Boolean) extends Chat

case class Supergroup(id: Long,
                      title: String,
                      username: Option[String]) extends Chat

case class Channel(id: Long,
                   title: String,
                   username: Option[String]) extends Chat

