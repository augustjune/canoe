package canoe.models

import java.util.NoSuchElementException

import canoe.marshalling._
import io.circe.{Decoder, Encoder}

/**
  * Different types of in-message entities.
  */
object MessageEntityType extends Enumeration {
  type MessageEntityType = Value

  val Bold, BotCommand, Cashtag, Code, Email, Hashtag, Italic, Mention, PhoneNumber, Pre, TextLink, TextMention,
  Unknown, Url = Value

  implicit val messageEntityTypeEncoder: Encoder[MessageEntityType] =
    Encoder[String].contramap[MessageEntityType](_.toString)

  implicit val messageEntityTypeDecoder: Decoder[MessageEntityType] =
    Decoder[String].map { s =>
      try {
        MessageEntityType.withName(s.pascalCase)
      } catch {
        case _: NoSuchElementException =>
          //            logger.warn(s"Unexpected MessageEntityType: '$s', fallback to Unknown.")
          MessageEntityType.Unknown
      }
    }
}
