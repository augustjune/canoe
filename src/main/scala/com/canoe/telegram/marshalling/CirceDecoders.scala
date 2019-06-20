package com.canoe.telegram.marshalling

import java.util.NoSuchElementException

import cats.syntax.functor._
import com.canoe.telegram.models.ChatAction.ChatAction
import com.canoe.telegram.models.ChatType.ChatType
import com.canoe.telegram.models.CountryCode.CountryCode
import com.canoe.telegram.models.Currency.Currency
import com.canoe.telegram.models.MaskPositionType.MaskPositionType
import com.canoe.telegram.models._
import com.canoe.telegram.models.MemberStatus.MemberStatus
import com.canoe.telegram.models.MessageEntityType.MessageEntityType
import com.canoe.telegram.models.ParseMode.ParseMode
import com.canoe.telegram.models.UpdateType.UpdateType
import com.canoe.telegram.models.messages._
import io.circe.Decoder
import io.circe.generic.semiauto._

/** Circe marshalling borrowed/inspired from [[https://github.com/nikdon/telepooz]]
  */
trait CirceDecoders  {

  implicit val memberStatusDecoder: Decoder[MemberStatus] =
    Decoder[String].map(s => MemberStatus.withName(marshalling.pascalize(s)))
  implicit val maskPositionTypeDecoder: Decoder[MaskPositionType] =
    Decoder[String].map(s => MaskPositionType.withName(marshalling.pascalize(s)))

  implicit val chatTypeDecoder: Decoder[ChatType] =
    Decoder[String].map(s => ChatType.withName(marshalling.pascalize(s)))

  implicit val messageEntityTypeDecoder: Decoder[MessageEntityType] =
    Decoder[String].map {
      s =>
        try {
          MessageEntityType.withName(marshalling.pascalize(s))
        } catch {
          case e: NoSuchElementException =>
//            logger.warn(s"Unexpected MessageEntityType: '$s', fallback to Unknown.")
            MessageEntityType.Unknown
        }
    }

  implicit val parseModeDecoder: Decoder[ParseMode] =
    Decoder[String].map(s => ParseMode.withName(marshalling.pascalize(s)))

  implicit val countryCodeDecoder: Decoder[CountryCode] =
    Decoder[String].map(a => CountryCode.withName(a))

  implicit val currencyDecoder: Decoder[Currency] =
    Decoder[String].map(a => Currency.withName(a))

  implicit val chatIdDecoder: Decoder[ChatId] =
    Decoder[String].map(ChatId.Channel) or Decoder[Long].map(ChatId.Chat)

  implicit val chatActionDecoder: Decoder[ChatAction] =
    Decoder[String].map(s => ChatAction.withName(marshalling.pascalize(s)))

  implicit val updateTypeDecoder: Decoder[UpdateType] =
    Decoder[String].map(s => UpdateType.withName(marshalling.pascalize(s)))

  implicit val audioDecoder: Decoder[Audio] = deriveDecoder[Audio]

  implicit val detailedChatDecoder: Decoder[DetailedChat] = deriveDecoder[DetailedChat]

  implicit val chatDecoder: Decoder[Chat] = Decoder.instance {
    cursor => cursor.get[ChatType]("type").map {
      case ChatType.Private => deriveDecoder[PrivateChat]
      case ChatType.Group => deriveDecoder[GroupChat]
      case ChatType.Supergroup => deriveDecoder[Supergroup]
      case ChatType.Channel => deriveDecoder[Channel]
    }.flatMap(_.tryDecode(cursor))
  }

  implicit val chatPhotoDecoder: Decoder[ChatPhoto] = deriveDecoder[ChatPhoto]

  implicit val contactDecoder: Decoder[Contact] = deriveDecoder[Contact]
  implicit val documentDecoder: Decoder[Document] = deriveDecoder[Document]
  implicit val fileDecoder: Decoder[File] = deriveDecoder[File]
  implicit val callbackGameDecoder: Decoder[CallbackGame] = deriveDecoder[CallbackGame]
  implicit val inlineKeyboardButtonDecoder: Decoder[InlineKeyboardButton] = deriveDecoder[InlineKeyboardButton]
  implicit val keyboardButtonDecoder: Decoder[KeyboardButton] = deriveDecoder[KeyboardButton]
  implicit val locationDecoder: Decoder[Location] = deriveDecoder[Location]

  implicit val messageEntityDecoder: Decoder[MessageEntity] = deriveDecoder[MessageEntity]

  implicit val webhookInfoDecoder: Decoder[WebhookInfo] = deriveDecoder[WebhookInfo]

  implicit val photoSizeDecoder: Decoder[PhotoSize] = deriveDecoder[PhotoSize]

  implicit val replyMarkupDecoder: Decoder[ReplyMarkup] = deriveDecoder[ReplyMarkup]

  implicit val stickerDecoder: Decoder[Sticker] = deriveDecoder[Sticker]

  implicit val callbackQueryDecoder: Decoder[CallbackQuery] = deriveDecoder[CallbackQuery]

  implicit val stickerSetDecoder: Decoder[StickerSet] = deriveDecoder[StickerSet]

  implicit val chatMemberDecoder: Decoder[ChatMember] =
    Decoder.instance {
      cursor =>
        cursor.get[MemberStatus]("status").map {
          case MemberStatus.Creator => deriveDecoder[ChatCreator]
          case MemberStatus.Administrator => deriveDecoder[ChatAdministrator]
          case MemberStatus.Member => deriveDecoder[CurrentMember]
          case MemberStatus.Restricted => deriveDecoder[RestrictedMember]
          case MemberStatus.Left => deriveDecoder[LeftMember]
          case MemberStatus.Kicked => deriveDecoder[KickedMember]
        }.flatMap(_.tryDecode(cursor))
    }


  implicit val maskPositionDecoder: Decoder[MaskPosition] = deriveDecoder[MaskPosition]

  implicit val userDecoder: Decoder[User] = deriveDecoder[User]
  implicit val userProfilePhotosDecoder: Decoder[UserProfilePhotos] = deriveDecoder[UserProfilePhotos]
  implicit val venueDecoder: Decoder[Venue] = deriveDecoder[Venue]
  implicit val videoDecoder: Decoder[Video] = deriveDecoder[Video]
  implicit val videoNoteDecoder: Decoder[VideoNote] = deriveDecoder[VideoNote]
  implicit val voiceDecoder: Decoder[Voice] = deriveDecoder[Voice]

  implicit val gameHighScoreDecoder: Decoder[GameHighScore] = deriveDecoder[GameHighScore]
  implicit val animationDecoder: Decoder[Animation] = deriveDecoder[Animation]
  implicit val gameDecoder: Decoder[Game] = deriveDecoder[Game]

  implicit val inlineQueryDecoder: Decoder[InlineQuery] = deriveDecoder[InlineQuery]
  implicit val chosenInlineQueryDecoder: Decoder[ChosenInlineResult] = deriveDecoder[ChosenInlineResult]

  implicit val inputContactMessageContent: Decoder[InputContactMessageContent] =
    deriveDecoder[InputContactMessageContent]
  implicit val inputVenueMessageContentDecoder: Decoder[InputVenueMessageContent] =
    deriveDecoder[InputVenueMessageContent]
  implicit val inputLocationMessageContentDecoder: Decoder[InputLocationMessageContent] =
    deriveDecoder[InputLocationMessageContent]
  implicit val inputTextMessageContentDecoder: Decoder[InputTextMessageContent] =
    deriveDecoder[InputTextMessageContent]

  implicit val labeledPriceDecoder: Decoder[LabeledPrice] = deriveDecoder[LabeledPrice]
  implicit val invoiceDecoder: Decoder[Invoice] = deriveDecoder[Invoice]
  implicit val shippingAddressDecoder: Decoder[ShippingAddress] = deriveDecoder[ShippingAddress]

  implicit val pollDecoder: Decoder[Poll] = deriveDecoder[Poll]
  implicit val pollOptionDecoder: Decoder[PollOption] = deriveDecoder[PollOption]

  implicit val shippingQueryDecoder: Decoder[ShippingQuery] = deriveDecoder[ShippingQuery]
  implicit val orderInfoDecoder: Decoder[OrderInfo] = deriveDecoder[OrderInfo]
  implicit val preCheckoutQueryDecoder: Decoder[PreCheckoutQuery] = deriveDecoder[PreCheckoutQuery]
  implicit val shippingOptionDecoder: Decoder[ShippingOption] = deriveDecoder[ShippingOption]
  implicit val successfulPaymentDecoder: Decoder[SuccessfulPayment] = deriveDecoder[SuccessfulPayment]

  implicit val responseParametersDecoder: Decoder[ResponseParameters] = deriveDecoder[ResponseParameters]

  implicit val updateDecoder: Decoder[Update] =
    List[Decoder[Update]](
      deriveDecoder[ReceivedMessage].widen,
      deriveDecoder[EditedMessage].widen,
      deriveDecoder[ChannelPost].widen,
      deriveDecoder[EditedChannelPost].widen,
      deriveDecoder[PollUpdate].widen,
      deriveDecoder[ReceivedInlineQuery].widen,
      deriveDecoder[ReceivedChosenInlineResult].widen,
      deriveDecoder[ReceivedCallbackQuery].widen,
      deriveDecoder[ReceivedShippingQuery].widen,
      deriveDecoder[ReceivedPreCheckoutQuery].widen
    ).reduceLeft(_ or _)

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

  implicit def responseDecoder[T](implicit decT: Decoder[T]): Decoder[Response[T]] = deriveDecoder[Response[T]]

  implicit def eitherDecoder[A, B](implicit decA: Decoder[A], decB: Decoder[B]): Decoder[Either[A, B]] = {
    val l: Decoder[Either[A, B]] = decA.map(Left.apply)
    val r: Decoder[Either[A, B]] = decB.map(Right.apply)
    l or r
  }
}

object CirceDecoders extends CirceDecoders
