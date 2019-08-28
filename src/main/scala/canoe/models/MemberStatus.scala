package canoe.models

import io.circe.Encoder

object MemberStatus extends Enumeration {
  type MemberStatus = Value
  val Creator, Administrator, Member, Restricted, Left, Kicked = Value

  implicit val memberStatusEncoder: Encoder[MemberStatus] =
    Encoder[String].contramap(_.toString)
}
