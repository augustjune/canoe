package canoe.api.models

import canoe.clients.RequestHandler
import canoe.methods.chats._
import canoe.methods.messages.SendMediaGroup
import canoe.models.ChatAction.ChatAction
import canoe.models._
import canoe.models.messages.TelegramMessage
import canoe.models.outgoing._
import cats.Applicative

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

  def administrators(implicit F: Applicative[F]): F[Seq[ChatMember]] =
    chat match {
      case _: PrivateChat => F.pure(Seq.empty)
      case _ => client.execute(GetChatAdministrators(chat.id))
    }

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

  def details: F[DetailedChat] =
    client.execute(GetChat(chat.id))

  def send(message: BotMessage): F[TelegramMessage] =
    client.execute(message.toRequest(chat.id))

  /**
    * Sends a list of media files as an album
    * @param media Must include 2-10 items
    */
  def sendAlbum(media: List[InputMedia], disableNotification: Boolean = false): F[List[TelegramMessage]] =
    client.execute(SendMediaGroup(chat.id, media, disableNotification = Some(disableNotification)))

}
