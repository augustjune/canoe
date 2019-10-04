package canoe.models.outgoing

import canoe.models.Currency.Currency
import canoe.models.ParseMode.ParseMode
import canoe.models.{InputFile, LabeledPrice}

sealed trait MessageContent

case class AnimationContent(animation: InputFile,
                            caption: String = "",
                            duration: Option[Int] = None,
                            width: Option[Int] = None,
                            height: Option[Int] = None,
                            thumb: Option[InputFile] = None,
                            parseMode: Option[ParseMode] = None)
    extends MessageContent

case class AudioContent(audio: InputFile,
                        caption: String = "",
                        duration: Option[Int] = None,
                        parseMode: Option[ParseMode] = None,
                        performer: Option[String] = None,
                        title: Option[String] = None,
                        thumb: Option[InputFile] = None)
    extends MessageContent

case class ContactContent(phoneNumber: String,
                          firstName: String,
                          lastName: Option[String] = None,
                          vcard: Option[String] = None)
    extends MessageContent

case class DocumentContent(document: InputFile,
                           thumb: Option[InputFile] = None,
                           caption: String = "",
                           parseMode: Option[ParseMode] = None)
    extends MessageContent

case class GameContent(gameShortName: String) extends MessageContent

case class InvoiceContent(title: String,
                          description: String,
                          payload: String,
                          providerToken: String,
                          startParameter: String,
                          currency: Currency,
                          prices: Array[LabeledPrice],
                          providerData: Option[String] = None,
                          photoUrl: Option[String] = None,
                          photoSize: Option[Int] = None,
                          photoWidth: Option[Int] = None,
                          photoHeight: Option[Int] = None,
                          needName: Option[Boolean] = None,
                          needPhoneNumber: Option[Boolean] = None,
                          needEmail: Option[Boolean] = None,
                          needShippingAddress: Option[Boolean] = None,
                          isFlexible: Option[Boolean] = None)
    extends MessageContent

case class LocationContent(latitude: Double, longitude: Double, livePeriod: Option[Int] = None) extends MessageContent

case class TextContent(text: String, parseMode: Option[ParseMode] = None, disableWebPagePreview: Option[Boolean] = None)
    extends MessageContent

case class PhotoContent(photo: InputFile, caption: String = "", parseMode: Option[ParseMode] = None)
    extends MessageContent

case class PollContent(question: String, options: Array[String]) extends MessageContent

case class StickerContent(sticker: InputFile) extends MessageContent

case class VenueContent(latitude: Double,
                        longitude: Double,
                        title: String,
                        address: String,
                        foursquareId: Option[String] = None,
                        foursquareType: Option[String] = None)
    extends MessageContent

case class VideoContent(video: InputFile,
                        caption: String = "",
                        duration: Option[Int] = None,
                        width: Option[Int] = None,
                        height: Option[Int] = None,
                        thumb: Option[InputFile] = None,
                        parseMode: Option[ParseMode] = None,
                        supportsStreaming: Option[Boolean] = None)
    extends MessageContent

case class VideoNoteContent(videoNote: InputFile, duration: Option[Int] = None, length: Option[Int] = None)
    extends MessageContent

case class VoiceContent(voice: InputFile,
                        caption: String = "",
                        parseMode: Option[ParseMode] = None,
                        duration: Option[Int] = None)
    extends MessageContent
