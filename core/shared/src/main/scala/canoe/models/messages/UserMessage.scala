package canoe.models.messages

import canoe.models.{Chat, User}
import cats.syntax.functor._
import io.circe.Decoder
import io.circe.generic.auto._
import io.circe.generic.semiauto

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

  def viaBot: Option[User]
}

object UserMessage {

  implicit val userMessageDecoder: Decoder[UserMessage] =
    List[Decoder[UserMessage]](
      semiauto.deriveDecoder[AnimationMessage].widen,
      semiauto.deriveDecoder[AudioMessage].widen,
      semiauto.deriveDecoder[ContactMessage].widen,
      semiauto.deriveDecoder[DocumentMessage].widen,
      semiauto.deriveDecoder[GameMessage].widen,
      semiauto.deriveDecoder[InvoiceMessage].widen,
      semiauto.deriveDecoder[LocationMessage].widen,
      semiauto.deriveDecoder[PhotoMessage].widen,
      semiauto.deriveDecoder[PollMessage].widen,
      semiauto.deriveDecoder[StickerMessage].widen,
      semiauto.deriveDecoder[TextMessage].widen,
      semiauto.deriveDecoder[VenueMessage].widen,
      semiauto.deriveDecoder[VideoMessage].widen,
      semiauto.deriveDecoder[VideoNoteMessage].widen,
      semiauto.deriveDecoder[VoiceMessage].widen
    ).reduceLeft(_.or(_))
}
