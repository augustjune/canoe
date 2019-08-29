package canoe.models

import io.circe.{Decoder, Encoder}

/**
  * The part of the face relative to which the mask should be placed.
  * One of "forehead", "eyes", "mouth", or "chin".
  */
// ToDo - update
object MaskPositionType extends Enumeration {
  type MaskPositionType = Value
  val Forehead, Eyes, Mouth, Chin = Value

  implicit val maskPositionTypeEncoder: Encoder[MaskPositionType] =
    Encoder[String].contramap[MaskPositionType](_.toString)

  implicit val maskPositionTypeDecoder: Decoder[MaskPositionType] =
    Decoder[String].map(s => MaskPositionType.withName(s.capitalize))
}
