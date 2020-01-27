package canoe.models

import io.circe.Decoder
import io.circe.generic.semiauto._

/**
  * Represents one special entity in a text message.
  * For example, hashtags, usernames, URLs, etc.
  */
sealed trait MessageEntity {
  def offset: Int
  def length: Int
}

object MessageEntity {
  import io.circe.generic.auto._

  implicit val chatDecoder: Decoder[MessageEntity] = Decoder.instance[MessageEntity] { cursor =>
    cursor
      .get[String]("type")
      .map {
        case "mention"       => deriveDecoder[Mention]
        case "hashtag"       => deriveDecoder[Hashtag]
        case "cashtag"       => deriveDecoder[Cashtag]
        case "url"           => deriveDecoder[Url]
        case "email"         => deriveDecoder[Email]
        case "phone_number"  => deriveDecoder[PhoneNumber]
        case "bold"          => deriveDecoder[Bold]
        case "italic"        => deriveDecoder[Italic]
        case "code"          => deriveDecoder[Code]
        case "pre"           => deriveDecoder[Pre]
        case "text_link"     => deriveDecoder[TextLink]
        case "text_mention"  => deriveDecoder[TextMention]
        case "underline"     => deriveDecoder[Underline]
        case "strikethrough" => deriveDecoder[Strikethrough]
        case _               => deriveDecoder[Unknown]
      }
      .flatMap(_.tryDecode(cursor))
  }

  /** '@username' */
  case class Mention(offset: Int, length: Int) extends MessageEntity

  case class Hashtag(offset: Int, length: Int) extends MessageEntity

  case class Cashtag(offset: Int, length: Int) extends MessageEntity

  case class BotCommand(offset: Int, length: Int) extends MessageEntity

  case class Url(offset: Int, length: Int) extends MessageEntity

  case class Email(offset: Int, length: Int) extends MessageEntity

  case class PhoneNumber(offset: Int, length: Int) extends MessageEntity

  /** Bold text */
  case class Bold(offset: Int, length: Int) extends MessageEntity

  /** Italic text */
  case class Italic(offset: Int, length: Int) extends MessageEntity

  /** Monowidth string */
  case class Code(offset: Int, length: Int) extends MessageEntity

  /** Monowidth block */
  case class Pre(offset: Int, length: Int, language: Option[String]) extends MessageEntity

  /** Clickable text URLs */
  case class TextLink(offset: Int, length: Int, url: String) extends MessageEntity

  /** Users without username */
  case class TextMention(offset: Int, length: Int, user: User) extends MessageEntity

  case class Underline(offset: Int, length: Int) extends MessageEntity

  case class Strikethrough(offset: Int, length: Int) extends MessageEntity

  case class Unknown(offset: Int, length: Int) extends MessageEntity
}
