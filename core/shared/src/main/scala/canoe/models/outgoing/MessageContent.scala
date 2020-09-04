package canoe.models.outgoing

import canoe.models.Currency.Currency
import canoe.models.ParseMode.ParseMode
import canoe.models.messages._
import canoe.models.{DiceEmoji, InputFile, LabeledPrice, ParseMode}

/**
  * The content of the message which is going to be sent by the bot.
  *
  * @tparam A Type of message which is going to be the result of sending this content to the Telegram
  */
sealed trait MessageContent[A]

final case class AnimationContent(animation: InputFile,
                                  caption: String = "",
                                  duration: Option[Int] = None,
                                  width: Option[Int] = None,
                                  height: Option[Int] = None,
                                  thumb: Option[InputFile] = None,
                                  parseMode: Option[ParseMode] = None
) extends MessageContent[AnimationMessage]

final case class AudioContent(audio: InputFile,
                              caption: String = "",
                              duration: Option[Int] = None,
                              parseMode: Option[ParseMode] = None,
                              performer: Option[String] = None,
                              title: Option[String] = None,
                              thumb: Option[InputFile] = None
) extends MessageContent[AudioMessage]

final case class ContactContent(phoneNumber: String,
                                firstName: String,
                                lastName: Option[String] = None,
                                vcard: Option[String] = None
) extends MessageContent[ContactMessage]

final case class DocumentContent(document: InputFile,
                                 thumb: Option[InputFile] = None,
                                 caption: String = "",
                                 parseMode: Option[ParseMode] = None
) extends MessageContent[DocumentMessage]

final case class GameContent(gameShortName: String) extends MessageContent[GameMessage]

final case class InvoiceContent(title: String,
                                description: String,
                                payload: String,
                                providerToken: String,
                                startParameter: String,
                                currency: Currency,
                                prices: Seq[LabeledPrice],
                                providerData: Option[String] = None,
                                photoUrl: Option[String] = None,
                                photoSize: Option[Int] = None,
                                photoWidth: Option[Int] = None,
                                photoHeight: Option[Int] = None,
                                needName: Option[Boolean] = None,
                                needPhoneNumber: Option[Boolean] = None,
                                needEmail: Option[Boolean] = None,
                                needShippingAddress: Option[Boolean] = None,
                                isFlexible: Option[Boolean] = None
) extends MessageContent[InvoiceMessage]

final case class LocationContent(latitude: Double, longitude: Double, livePeriod: Option[Int] = None)
    extends MessageContent[LocationMessage]

final case class TextContent(text: String,
                             parseMode: Option[ParseMode] = None,
                             disableWebPagePreview: Option[Boolean] = None
) extends MessageContent[TextMessage] {

  /**
    * Text content with markdown parse mode.
    *
    * Markdown formatting applied according to MarkdownV2 style.
    * Examples and additional information can be found [[https://core.telegram.org/bots/api#markdownv2-style here]].
    */
  def markdown: TextContent = copy(parseMode = Some(ParseMode.MarkdownV2))

  /**
    * Text content with legacy markdown parse mode.
    *
    * Markdown formatting applied according to Markdown style.
    * Examples and additional information can be found [[https://core.telegram.org/bots/api#markdown-style here]].
    */
  def markdownOld: TextContent = copy(parseMode = Some(ParseMode.Markdown))

  /**
    * Text content with HTML parse mode.
    *
    * Examples and additional information can be found [[https://core.telegram.org/bots/api#html-style here]].
    */
  def html: TextContent = copy(parseMode = Some(ParseMode.HTML))
}

final case class PhotoContent(photo: InputFile, caption: String = "", parseMode: Option[ParseMode] = None)
    extends MessageContent[PhotoMessage]

final case class PollContent(question: String,
                             options: List[String],
                             allowsMultipleAnswers: Boolean = false,
                             anonymous: Boolean = false,
                             openPeriod: Option[Int] = None,
                             closeDate: Option[Int] = None
) extends MessageContent[PollMessage]

final case class QuizContent(question: String,
                             options: List[String],
                             correctOptionId: Int,
                             anonymous: Boolean = false,
                             explanation: Option[String] = None,
                             explanationParseMode: Option[ParseMode] = None,
                             openPeriod: Option[Int] = None,
                             closeDate: Option[Int] = None
) extends MessageContent[PollMessage]

final case class StickerContent(sticker: InputFile) extends MessageContent[StickerMessage]

final case class VenueContent(latitude: Double,
                              longitude: Double,
                              title: String,
                              address: String,
                              foursquareId: Option[String] = None,
                              foursquareType: Option[String] = None
) extends MessageContent[VenueMessage]

final case class VideoContent(video: InputFile,
                              caption: String = "",
                              duration: Option[Int] = None,
                              width: Option[Int] = None,
                              height: Option[Int] = None,
                              thumb: Option[InputFile] = None,
                              parseMode: Option[ParseMode] = None,
                              supportsStreaming: Option[Boolean] = None
) extends MessageContent[VideoMessage]

final case class VideoNoteContent(videoNote: InputFile, duration: Option[Int] = None, length: Option[Int] = None)
    extends MessageContent[VideoNoteMessage]

final case class VoiceContent(voice: InputFile,
                              caption: String = "",
                              parseMode: Option[ParseMode] = None,
                              duration: Option[Int] = None
) extends MessageContent[VoiceMessage]

final case class DiceContent(emoji: DiceEmoji) extends MessageContent[DiceThrownMessage]
