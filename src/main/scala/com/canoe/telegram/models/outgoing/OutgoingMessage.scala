package com.canoe.telegram.models.outgoing

import com.canoe.telegram.models.ParseMode.ParseMode
import com.canoe.telegram.models.{InputFile, ReplyMarkup}

////////////////////////////////////////////////
//////////  One way (less code duplication, separation message from its content)
////////////////////////////////////////////////
case class OutMessage(content: MessageContent,
                      disableNotification: Option[Boolean] = None,
                      replyToMessageId: Option[Int] = None,
                      replyMarkup: Option[ReplyMarkup] = None) {
  def silentMode(value: Boolean): OutMessage =
    copy(disableNotification = Some(value))

  def withReplyMarkup(markup: ReplyMarkup): OutMessage =
    copy(replyMarkup = Some(markup))

  def asReplyToMessage(messageId: Int): OutMessage =
    copy(replyToMessageId = Some(messageId))
}

sealed trait MessageContent

case class TextContent(text: String,
                       parseMode: Option[ParseMode] = None,
                       disableWebPagePreview: Option[Boolean] = None
                      ) extends MessageContent {
  def withParseMode(mode: ParseMode): TextContent =
    copy(parseMode = Some(mode))

  def withWebPagePreview(value: Boolean): TextContent =
    copy(disableWebPagePreview = Some(!value))
}

case class AudioContent(audio: InputFile,
                        duration: Option[Int] = None,
                        caption: Option[String] = None,
                        parseMode: Option[ParseMode] = None,
                        performer: Option[String] = None,
                        title: Option[String] = None
                       ) extends MessageContent {
  def withCaption(text: String): AudioContent =
    copy(caption = Some(text))

  def withParseMode(mode: ParseMode): AudioContent =
    copy(parseMode = Some(mode))

  def withPerformer(name: String): AudioContent =
    copy(performer = Some(name))

  def withTitle(name: String): AudioContent =
    copy(title = Some(name))

  def withDuration(seconds: Int): AudioContent =
    copy(duration = Some(seconds))
}

case class DocumentContent(document: InputFile,
                           caption: Option[String] = None,
                           parseMode: Option[ParseMode] = None
                          ) extends MessageContent {
  def withCaption(text: String): DocumentContent =
    copy(caption = Some(text))

  def withParseMode(mode: ParseMode): DocumentContent =
    copy(parseMode = Some(mode))
}


////////////////////////////////////////////////
//////////  Second way (code duplication, content of the message is part of the message)
////////////////////////////////////////////////

sealed trait OutgoingMessage

case class TextMessage(text: String,
                       parseMode: Option[ParseMode] = None,
                       disableWebPagePreview: Option[Boolean] = None,
                       disableNotification: Option[Boolean] = None,
                       replyToMessageId: Option[Int] = None,
                       replyMarkup: Option[ReplyMarkup] = None
                      ) extends OutgoingMessage {

  def withParseMode(mode: ParseMode): TextMessage =
    copy(parseMode = Some(mode))

  def withWebPagePreview(value: Boolean): TextMessage =
    copy(disableWebPagePreview = Some(!value))

  def silentMode(value: Boolean): TextMessage =
    copy(disableNotification = Some(value))

  def withReplyMarkup(markup: ReplyMarkup): TextMessage =
    copy(replyMarkup = Some(markup))

  def asReplyToMessage(messageId: Int): TextMessage =
    copy(replyToMessageId = Some(messageId))
}

case class AudioMessage(audio: InputFile,
                        duration: Option[Int] = None,
                        caption: Option[String] = None,
                        parseMode: Option[ParseMode] = None,
                        performer: Option[String] = None,
                        title: Option[String] = None,
                        disableNotification: Option[Boolean] = None,
                        replyToMessageId: Option[Long] = None,
                        replyMarkup: Option[ReplyMarkup] = None
                       ) extends OutgoingMessage {

  def withDuration(seconds: Int): AudioMessage =
    copy(duration = Some(seconds))

  def withCaption(text: String): AudioMessage =
    copy(caption = Some(text))

  def withParseMode(mode: ParseMode): AudioMessage =
    copy(parseMode = Some(mode))

  def withPerformer(name: String): AudioMessage =
    copy(performer = Some(name))

  def withTitle(name: String): AudioMessage =
    copy(title = Some(name))

  def silentMode(value: Boolean): AudioMessage =
    copy(disableNotification = Some(value))

  def withReplyMarkup(markup: ReplyMarkup): AudioMessage =
    copy(replyMarkup = Some(markup))

  def asReplyToMessage(messageId: Int): AudioMessage =
    copy(replyToMessageId = Some(messageId))
}

case class DocumentMessage(document: InputFile,
                           caption: Option[String] = None,
                           parseMode: Option[ParseMode] = None,
                           disableNotification: Option[Boolean] = None,
                           replyToMessageId: Option[Long] = None,
                           replyMarkup: Option[ReplyMarkup] = None
                          ) extends OutgoingMessage {

  def silentMode(value: Boolean): DocumentMessage =
    copy(disableNotification = Some(value))

  def withReplyMarkup(markup: ReplyMarkup): DocumentMessage =
    copy(replyMarkup = Some(markup))

  def asReplyToMessage(messageId: Int): DocumentMessage =
    copy(replyToMessageId = Some(messageId))
}

