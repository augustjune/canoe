package canoe.models

import io.circe.Decoder

object MemberStatus extends Enumeration {
  type MemberStatus = Value
  val Creator, Administrator, Member, Restricted, Left, Kicked = Value

  implicit val memberStatusDecoder: Decoder[MemberStatus] =
    Decoder[String].map(s => MemberStatus.withName(s.capitalize))

}
