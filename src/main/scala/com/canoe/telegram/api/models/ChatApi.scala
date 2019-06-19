package com.canoe.telegram.api.models

import com.canoe.telegram.clients.RequestHandler
import com.canoe.telegram.methods.chats._
import com.canoe.telegram.models.ChatAction.ChatAction
import com.canoe.telegram.models._
import com.canoe.telegram.models.messages.TelegramMessage
import com.canoe.telegram.models.outgoing._

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
  def pinMessage(message: TelegramMessage): F[Boolean] =
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

  def sendMessage(message: BotMessage): F[TelegramMessage] =
    client.execute(message.toRequest(chat.id))

}
