package canoe.models

sealed trait Chat {
  def id: Long
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

