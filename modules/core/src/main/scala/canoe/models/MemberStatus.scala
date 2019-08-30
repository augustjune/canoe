package canoe.models

import io.circe.{Decoder, Encoder}

object MemberStatus extends Enumeration {
  type MemberStatus = Value
  val Creator, Administrator, Member, Restricted, Left, Kicked = Value

  implicit val memberStatusEncoder: Encoder[MemberStatus] =
    Encoder[String].contramap(_.toString)

  implicit val memberStatusDecoder: Decoder[MemberStatus] =
    Decoder[String].map(s => MemberStatus.withName(s.capitalize))

}
