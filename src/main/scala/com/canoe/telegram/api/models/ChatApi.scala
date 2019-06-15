package com.canoe.telegram.api.models

import com.canoe.telegram.clients.RequestHandler
import com.canoe.telegram.methods.chats._
import com.canoe.telegram.methods.messages._
import com.canoe.telegram.models._
import com.canoe.telegram.models.ChatAction.ChatAction
import com.canoe.telegram.models.ParseMode.ParseMode

final class ChatApi[F[_]](chat: Chat)
                         (implicit client: RequestHandler[F]) {

  def setAction(action: ChatAction): F[Boolean] =
    client.execute(SendChatAction(chat.id, action))

  def deletePhoto: F[Boolean] =
    client.execute(DeleteChatPhoto(chat.id))

  def deleteStickerSet: F[Boolean] =
    client.execute(DeleteChatStickerSet(chat.id))

  def exportInviteLink: F[String] =
    client.execute(ExportChatInviteLink(chat.id))

  def administrators: F[Seq[ChatMember]] =
    client.execute(GetChatAdministrators(chat.id))

  def getMember(user: User): F[ChatMember] =
    client.execute(GetChatMember(chat.id, user.id))

  def membersCount: F[Int] =
    client.execute(GetChatMembersCount(chat.id))

  // ToDo - add parameters
  def kick(user: User): F[Boolean] =
    client.execute(KickChatMember(chat.id, user.id))

  def leave: F[Boolean] =
    client.execute(LeaveChat(chat.id))

  // ToDo - add parameters
  def pinMessage(message: Message): F[Boolean] =
    client.execute(PinChatMessage(chat.id, message.messageId))

  // ToDo - add parameters
  def promoteMember(user: User): F[Boolean] =
    client.execute(PromoteChatMember(chat.id, user.id))

  def restrictMember(user: User): F[Boolean] =
    client.execute(RestrictChatMember(chat.id, user.id))

  def setDescription(description: String): F[Boolean] =
    client.execute(SetChatDescription(chat.id, Option(description)))

  def setTitle(title: String): F[Boolean] =
    client.execute(SetChatTitle(chat.id, title))

  def unbanMember(user: User): F[Boolean] =
    client.execute(UnbanChatMember(chat.id, user.id))

  def unpinMessage: F[Boolean] =
    client.execute(UnpinChatMessage(chat.id))

  // ToDo - naming stuff
  def reread: F[Chat] =
    client.execute(GetChat(chat.id))

  ///////////////////
  //        Messages
  ///////////////////

  def sendMessage(text: String,
                  parseMode: Option[ParseMode] = None,
                  disableLinkPreview: Option[Boolean] = None,
                  silent: Option[Boolean] = None,
                  markup: Option[ReplyMarkup] = None): F[Message] =
    client.execute(SendMessage(chat.id, text, parseMode, disableLinkPreview, silent, replyMarkup = markup))

  def sendAnimation(animation: InputFile,
                    caption: Option[String] = None): F[Message] =
    client.execute(SendAnimation(chat.id, animation, caption = caption))

  // ToDo - add parameters
  def sendAudio(audio: InputFile,
                caption: Option[String] = None): F[Message] =
    client.execute(SendAudio(chat.id, audio, caption = caption))

  // ToDo - add parameters
  def sendContact(phoneNumber: String,
                  firstName: String,
                  lastName: Option[String] = None): F[Message] =
    client.execute(SendContact(chat.id, phoneNumber, firstName, lastName))

  def sendDocument(document: InputFile,
                   caption: Option[String] = None): F[Message] =
    client.execute(SendDocument(chat.id, document, caption = caption))

  def sendGame(game: String,
               silent: Option[Boolean] = None,
               markup: Option[ReplyMarkup] = None): F[Message] =
    client.execute(SendGame(chat.id, game, silent, replyMarkup = markup))


  //  def sendInvoice(title: String)

  def sendLocation(lat: Double,
                   lon: Double,
                   livePeriod: Option[Int],
                   silent: Option[Boolean] = None,
                   markup: Option[ReplyMarkup] = None): F[Message] =
    client.execute(SendLocation(chat.id, lat, lon, livePeriod, silent, replyMarkup = markup))

  def sendPhoto(photo: InputFile,
                caption: Option[String] = None,
                parseMode: Option[ParseMode],
                silent: Option[Boolean] = None,
                markup: Option[ReplyMarkup] = None): F[Message] =
    client.execute(SendPhoto(chat.id, photo, caption, parseMode, silent, replyMarkup = markup))

  def sendSticker(sticker: InputFile,
                  silent: Option[Boolean] = None,
                  markup: Option[ReplyMarkup] = None): F[Message] =
    client.execute(SendSticker(chat.id, sticker, silent, replyMarkup = markup))

  def sendPoll(question: String,
               options: List[String],
               silent: Option[Boolean] = None,
               markup: Option[ReplyMarkup] = None): F[Message] =
    client.execute(SendPoll(chat.id, question, options.toArray, silent, replyMarkup = markup))
}
