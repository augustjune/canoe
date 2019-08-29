package canoe.models.messages

import canoe.models.Chat
import cats.syntax.functor._
import io.circe.Decoder
import io.circe.generic.auto._
import io.circe.generic.semiauto.deriveDecoder

trait TelegramMessage {
  def messageId: Int
  def chat: Chat
  def date: Int
}

object TelegramMessage {

  implicit val telegramMessageDecoder: Decoder[TelegramMessage] =
    List[Decoder[TelegramMessage]](
      deriveDecoder[AnimationMessage].widen,
      deriveDecoder[AudioMessage].widen,
      deriveDecoder[ChannelCreated].widen,
      deriveDecoder[ChatMemberAdded].widen,
      deriveDecoder[ChatMemberLeft].widen,
      deriveDecoder[ChatPhotoChanged].widen,
      deriveDecoder[ChatPhotoDeleted].widen,
      deriveDecoder[ChatTitleChanged].widen,
      deriveDecoder[ContactMessage].widen,
      deriveDecoder[DocumentMessage].widen,
      deriveDecoder[GameMessage].widen,
      deriveDecoder[InvoiceMessage].widen,
      deriveDecoder[LocationMessage].widen,
      deriveDecoder[MessagePinned].widen,
      deriveDecoder[MigratedFromGroup].widen,
      deriveDecoder[MigratedToSupergroup].widen,
      deriveDecoder[PhotoMessage].widen,
      deriveDecoder[PollMessage].widen,
      deriveDecoder[StickerMessage].widen,
      deriveDecoder[SuccessfulPaymentMessage].widen,
      deriveDecoder[SupergroupCreated].widen,
      deriveDecoder[TextMessage].widen,
      deriveDecoder[VenueMessage].widen,
      deriveDecoder[VideoMessage].widen,
      deriveDecoder[VideoNoteMessage].widen,
      deriveDecoder[VoiceMessage].widen,
      deriveDecoder[WebsiteConnected].widen
    ).reduceLeft(_ or _)
}
