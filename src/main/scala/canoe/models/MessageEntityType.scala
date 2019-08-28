package canoe.models

import io.circe.Encoder

/**
  * Different types of in-message entities.
  */
object MessageEntityType extends Enumeration {
  type MessageEntityType = Value

  val
  Bold,
  BotCommand,
  Cashtag,
  Code,
  Email,
  Hashtag,
  Italic,
  Mention,
  PhoneNumber,
  Pre,
  TextLink,
  TextMention,
  Unknown,
  Url = Value

  implicit val messageEntityTypeEncoder: Encoder[MessageEntityType] =
    Encoder[String].contramap[MessageEntityType](_.toString)
}
