package canoe.models

import io.circe.Decoder
import io.circe.generic.semiauto

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
        case "mention"       => semiauto.deriveDecoder[Mention]
        case "hashtag"       => semiauto.deriveDecoder[Hashtag]
        case "cashtag"       => semiauto.deriveDecoder[Cashtag]
        case "url"           => semiauto.deriveDecoder[Url]
        case "email"         => semiauto.deriveDecoder[Email]
        case "phone_number"  => semiauto.deriveDecoder[PhoneNumber]
        case "bold"          => semiauto.deriveDecoder[Bold]
        case "italic"        => semiauto.deriveDecoder[Italic]
        case "code"          => semiauto.deriveDecoder[Code]
        case "pre"           => semiauto.deriveDecoder[Pre]
        case "text_link"     => semiauto.deriveDecoder[TextLink]
        case "text_mention"  => semiauto.deriveDecoder[TextMention]
        case "underline"     => semiauto.deriveDecoder[Underline]
        case "strikethrough" => semiauto.deriveDecoder[Strikethrough]
        case _               => semiauto.deriveDecoder[Unknown]
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
