package canoe.models.messages

import canoe.models.{Chat, User}
import cats.syntax.functor._
import io.circe.Decoder
import io.circe.generic.auto._
import io.circe.generic.semiauto.deriveDecoder

trait UserMessage extends TelegramMessage {

  def from: Option[User]

  def forwardFrom: Option[User]

  def forwardFromChat: Option[Chat]

  def forwardFromMessageId: Option[Int]

  def forwardSignature: Option[String]

  def forwardSenderName: Option[String]

  def forwardDate: Option[Int]

  def replyToMessage: Option[TelegramMessage]

  def editDate: Option[Int]

  def authorSignature: Option[String]
}

object UserMessage {

  implicit val userMessageDecoder: Decoder[UserMessage] =
    List[Decoder[UserMessage]](
      deriveDecoder[AnimationMessage].widen,
      deriveDecoder[AudioMessage].widen,
      deriveDecoder[ContactMessage].widen,
      deriveDecoder[DocumentMessage].widen,
      deriveDecoder[GameMessage].widen,
      deriveDecoder[InvoiceMessage].widen,
      deriveDecoder[LocationMessage].widen,
      deriveDecoder[PhotoMessage].widen,
      deriveDecoder[PollMessage].widen,
      deriveDecoder[StickerMessage].widen,
      deriveDecoder[TextMessage].widen,
      deriveDecoder[VenueMessage].widen,
      deriveDecoder[VideoMessage].widen,
      deriveDecoder[VideoNoteMessage].widen,
      deriveDecoder[VoiceMessage].widen
    ).reduceLeft(_.or(_))
}
