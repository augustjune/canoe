package com.canoe.telegram.models.outgoing

import com.canoe.telegram.methods.Request
import com.canoe.telegram.methods.messages._
import com.canoe.telegram.models.ReplyMarkup
import com.canoe.telegram.models.messages.TelegramMessage

/**
  * Outgoing message, which is going to be sent by the bot
  */
case class BotMessage(content: MessageContent,
                      disableNotification: Option[Boolean] = None,
                      replyToMessageId: Option[Int] = None,
                      replyMarkup: Option[ReplyMarkup] = None) {

  def silentMode(value: Boolean): BotMessage =
    copy(disableNotification = Some(value))

  def withReplyMarkup(markup: ReplyMarkup): BotMessage =
    copy(replyMarkup = Some(markup))

  def asReplyToMessage(messageId: Int): BotMessage =
    copy(replyToMessageId = Some(messageId))

  // ToDo - think about other way of mapping BotMessage to send request (e.g.: optics?)
  def toRequest(chatId: Long): Request[TelegramMessage] = content match {
    case AnimationContent(animation, duration, width, height, thumb, caption, parseMode) =>
      SendAnimation(chatId, animation, duration, width, height, thumb, caption, parseMode, disableNotification, replyToMessageId, replyMarkup)

    case AudioContent(audio, duration, caption, parseMode, performer, title) =>
      SendAudio(chatId, audio, duration, caption, parseMode, performer, title, disableNotification, replyToMessageId, replyMarkup)

    case ContactContent(phoneNumber, firstName, lastName, vcard) =>
      SendContact(chatId, phoneNumber, firstName, lastName, vcard, disableNotification, replyToMessageId, replyMarkup)

    case DocumentContent(document, caption, parseMode) =>
      SendDocument(chatId, document, caption, parseMode, disableNotification, replyToMessageId, replyMarkup)

    case GameContent(gameShortName) =>
      SendGame(chatId, gameShortName, disableNotification, replyToMessageId, replyMarkup)

    case InvoiceContent(title, description, payload, providerToken, startParameter, currency, prices, providerData, photoUrl, photoSize, photoWidth, photoHeight, needName, needPhoneNumber, needEmail, needShippingAddress, isFlexible) =>
      SendInvoice(chatId, title, description, payload, providerToken, startParameter, currency, prices, providerData, photoUrl, photoSize, photoWidth, photoHeight, needName, needPhoneNumber, needEmail, needShippingAddress, isFlexible, disableNotification, replyToMessageId, replyMarkup)

    case LocationContent(latitude, longitude, livePeriod) =>
      SendLocation(chatId, latitude, longitude, livePeriod, disableNotification, replyToMessageId, replyMarkup)

      // ToDo - unhandled case
//    case MediaGroupContent(media) =>
//      SendMediaGroup(chatId, media, disableNotification, replyToMessageId)

    case TextContent(text, parseMode, disableWebPagePreview) =>
      SendMessage(chatId, text, parseMode, disableWebPagePreview, disableNotification, replyToMessageId, replyMarkup)

    case PhotoContent(photo, caption, parseMode) =>
      SendPhoto(chatId, photo, caption, parseMode, disableNotification, replyToMessageId, replyMarkup)

    case PollContent(question, options) =>
      SendPoll(chatId, question, options, disableNotification, replyToMessageId, replyMarkup)

    case StickerContent(sticker) =>
      SendSticker(chatId, sticker, disableNotification, replyToMessageId, replyMarkup)

    case VenueContent(latitude, longitude, title, address, foursquareId, foursquareType, duration) =>
      SendVenue(chatId, latitude, longitude, title, address, foursquareId, foursquareType, duration, disableNotification, replyToMessageId, replyMarkup)

    case VideoContent(video, duration, width, height, caption, parseMode, supportsStreaming) =>
      SendVideo(chatId, video, duration, width, height, caption, parseMode, supportsStreaming, disableNotification, replyToMessageId, replyMarkup)

    case VideoNoteContent(videoNote, duration, length) =>
      SendVideoNote(chatId, videoNote, duration, length, disableNotification, replyToMessageId, replyMarkup)

    case VoiceContent(voice, caption, parseMode, duration) =>
      SendVoice(chatId, voice, caption, parseMode, duration, disableNotification, replyToMessageId, replyMarkup)
  }
}
