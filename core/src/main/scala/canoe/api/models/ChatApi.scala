package canoe.api.models

import canoe.api.TelegramClient
import canoe.methods.chats._
import canoe.methods.messages._
import canoe.models.ChatAction.ChatAction
import canoe.models._
import canoe.models.messages.TelegramMessage
import canoe.models.outgoing._
import canoe.syntax.methodOps
import cats.Applicative

final class ChatApi[F[_]](chat: Chat)(implicit client: TelegramClient[F]) {

  /**
    * Sets default permissions for all chat members
    *
    * The bot must be an administrator in the group or a supergroup
    * and must have the can_restrict_members admin rights
    */
  def setDefaultPermissions(permissions: ChatPermissions)(implicit F: Applicative[F]): F[Boolean] =
    chat match {
      case _: Group | _: Supergroup =>
        SetChatPermissions(chat.id, permissions).call
      case _ => F.pure(false)
    }

  /**
    * Deletes the photo of the this chat
    */
  def deletePhoto: F[Boolean] =
    DeleteChatPhoto(chat.id).call

  /**
    * Deletes a group of stickers set for this chat
    */
  def deleteStickerSet: F[Boolean] =
    DeleteChatStickerSet(chat.id).call

  /**
    * Generates new invite link ofr this chat.
    *
    * Any previously generated link is revoked
    */
  def exportInviteLink: F[String] =
    ExportChatInviteLink(chat.id).call

  /**
    * @return list of administrators of this chat
    */
  def administrators(implicit F: Applicative[F]): F[List[ChatMember]] =
    chat match {
      case _: PrivateChat => F.pure(Nil)
      case _              => GetChatAdministrators(chat.id).call
    }

  /**
    * @return Detailed information about the member of this chat
    */
  def getMember(userId: Int): F[ChatMember] =
    GetChatMember(chat.id, userId).call

  /**
    * @return number of members of this chat
    */
  def membersCount: F[Int] =
    GetChatMembersCount(chat.id).call

  /**
    * Kicks a user from this chat
    */
  def kickUser(userId: Int, untilDate: Option[Int] = None)(implicit F: Applicative[F]): F[Boolean] =
    chat match {
      case _: PrivateChat => F.pure(false)
      case _              => KickChatMember(chat.id, userId, untilDate).call
    }

  /**
    * Abandons this chat
    */
  def leave(implicit F: Applicative[F]): F[Boolean] =
    chat match {
      case _: PrivateChat => F.pure(false)
      case _              => LeaveChat(chat.id).call
    }

  private def notFalse(b: Boolean): Option[Boolean] =
    if (b) Some(true)
    else None

  /**
    * Pins message in this chat, unless it's private chat
    */
  def pinMessage(messageId: Int, silent: Boolean = true)(implicit F: Applicative[F]): F[Boolean] =
    chat match {
      case _: PrivateChat => F.pure(false)
      case _              => PinChatMessage(chat.id, messageId, notFalse(silent)).call
    }

  /**
    * Promotes or demotes a user in this chat
    */
  def promoteMember(userId: Int,
                    canChangeInfo: Option[Boolean] = None,
                    canPostMessages: Option[Boolean] = None,
                    canEditMessages: Option[Boolean] = None,
                    canDeleteMessages: Option[Boolean] = None,
                    canInviteUsers: Option[Boolean] = None,
                    canRestrictMembers: Option[Boolean] = None,
                    canPinMessages: Option[Boolean] = None,
                    canPromoteMembers: Option[Boolean] = None)(implicit F: Applicative[F]): F[Boolean] =
    chat match {
      case _: Supergroup | _: Channel =>
        PromoteChatMember(chat.id,
                          userId,
                          canChangeInfo,
                          canPostMessages,
                          canDeleteMessages,
                          canInviteUsers,
                          canRestrictMembers,
                          canPinMessages,
                          canPromoteMembers).call

      case _ => F.pure(false)
    }

  /**
    * Restricts a user in a supergroup
    *
    * The bot must be an administrator in the supergroup
    * and must have the appropriate admin rights
    */
  def restrictMember(userId: Int, permissions: ChatPermissions, until: Option[Int] = None)(
    implicit F: Applicative[F]
  ): F[Boolean] =
    chat match {
      case _: Supergroup =>
        RestrictChatMember(chat.id, userId, permissions, until).call

      case _ => F.pure(false)
    }

  /**
    * Sets the status of the bot for this chat
    *
    * The action is set for 5 seconds or until
    * the first message is send by the bot
    */
  def setAction(action: ChatAction): F[Boolean] =
    SendChatAction(chat.id, action).call

  /**
    * Changes the description of this chat if it's a group, a supergroup or a channel
    */
  def setDescription(description: String)(implicit F: Applicative[F]): F[Boolean] =
    chat match {
      case _: Group | _: Supergroup | _: Channel =>
        SetChatDescription(chat.id, Option(description)).call

      case _ => F.pure(false)
    }

  /**
    * Changes the title of this chat, unless it's private chat
    */
  def setTitle(title: String)(implicit F: Applicative[F]): F[Boolean] =
    chat match {
      case _: PrivateChat => F.pure(false)
      case _              => SetChatTitle(chat.id, title).call
    }

  /**
    * Unbans previously kicked user
    */
  def unbanMember(userId: Int): F[Boolean] =
    UnbanChatMember(chat.id, userId).call

  /**
    * Unpins pinned chat message
    */
  def unpinMessage(implicit F: Applicative[F]): F[Boolean] =
    chat match {
      case _: PrivateChat => F.pure(false)
      case _              => UnpinChatMessage(chat.id).call
    }

  /**
    * @return Detailed information about this chat
    */
  def details: F[DetailedChat] =
    GetChat(chat.id).call

  private def nonEmpty(str: String): Option[String] =
    if (str.isEmpty) None
    else Some(str)

  /**
    * Sends a message to this chat
    */
  def send[M](content: MessageContent[M],
              replyToMessageId: Option[Int] = None,
              replyMarkup: Option[ReplyMarkup] = None,
              disableNotification: Boolean = false): F[M] =
    // Casting the result type due to the lack of type inference in M type parameter
    content match {
      case AnimationContent(animation, caption, duration, width, height, thumb, parseMode) =>
        SendAnimation(chat.id,
                      animation,
                      duration,
                      width,
                      height,
                      thumb,
                      nonEmpty(caption),
                      parseMode,
                      notFalse(disableNotification),
                      replyToMessageId,
                      replyMarkup).call.asInstanceOf[F[M]]

      case AudioContent(audio, caption, duration, parseMode, performer, title, thumb) =>
        SendAudio(chat.id,
                  audio,
                  duration,
                  nonEmpty(caption),
                  parseMode,
                  performer,
                  title,
                  thumb,
                  notFalse(disableNotification),
                  replyToMessageId,
                  replyMarkup).call.asInstanceOf[F[M]]

      case ContactContent(phoneNumber, firstName, lastName, vcard) =>
        SendContact(chat.id,
                    phoneNumber,
                    firstName,
                    lastName,
                    vcard,
                    notFalse(disableNotification),
                    replyToMessageId,
                    replyMarkup).call.asInstanceOf[F[M]]

      case DocumentContent(document, thumb, caption, parseMode) =>
        SendDocument(chat.id,
                     document,
                     thumb,
                     nonEmpty(caption),
                     parseMode,
                     notFalse(disableNotification),
                     replyToMessageId,
                     replyMarkup).call.asInstanceOf[F[M]]

      case GameContent(gameShortName) =>
        SendGame(chat.id, gameShortName, notFalse(disableNotification), replyToMessageId, replyMarkup).call.asInstanceOf[F[M]]

      case InvoiceContent(title,
                          description,
                          payload,
                          providerToken,
                          startParameter,
                          currency,
                          prices,
                          providerData,
                          photoUrl,
                          photoSize,
                          photoWidth,
                          photoHeight,
                          needName,
                          needPhoneNumber,
                          needEmail,
                          needShippingAddress,
                          isFlexible) =>
        SendInvoice(
          chat.id,
          title,
          description,
          payload,
          providerToken,
          startParameter,
          currency,
          prices,
          providerData,
          photoUrl,
          photoSize,
          photoWidth,
          photoHeight,
          needName,
          needPhoneNumber,
          needEmail,
          needShippingAddress,
          isFlexible,
          notFalse(disableNotification),
          replyToMessageId,
          replyMarkup
        ).call.asInstanceOf[F[M]]

      case LocationContent(latitude, longitude, livePeriod) =>
        SendLocation(chat.id, latitude, longitude, livePeriod, notFalse(disableNotification), replyToMessageId, replyMarkup).call
          .asInstanceOf[F[M]]

      case TextContent(text, parseMode, disableWebPagePreview) =>
        SendMessage(chat.id, text, parseMode, disableWebPagePreview, notFalse(disableNotification), replyToMessageId, replyMarkup).call
          .asInstanceOf[F[M]]

      case PhotoContent(photo, caption, parseMode) =>
        SendPhoto(chat.id, photo, nonEmpty(caption), parseMode, notFalse(disableNotification), replyToMessageId, replyMarkup).call
          .asInstanceOf[F[M]]

      case PollContent(question, options) =>
        SendPoll(chat.id, question, options, notFalse(disableNotification), replyToMessageId, replyMarkup).call
          .asInstanceOf[F[M]]

      case StickerContent(sticker) =>
        SendSticker(chat.id, sticker, notFalse(disableNotification), replyToMessageId, replyMarkup).call.asInstanceOf[F[M]]

      case VenueContent(latitude, longitude, title, address, foursquareId, foursquareType) =>
        SendVenue(chat.id,
                  latitude,
                  longitude,
                  title,
                  address,
                  foursquareId,
                  foursquareType,
                  notFalse(disableNotification),
                  replyToMessageId,
                  replyMarkup).call.asInstanceOf[F[M]]

      case VideoContent(video, caption, duration, width, height, thumb, parseMode, supportsStreaming) =>
        SendVideo(chat.id,
                  video,
                  duration,
                  width,
                  height,
                  thumb,
                  nonEmpty(caption),
                  parseMode,
                  supportsStreaming,
                  notFalse(disableNotification),
                  replyToMessageId,
                  replyMarkup).call.asInstanceOf[F[M]]

      case VideoNoteContent(videoNote, duration, length) =>
        SendVideoNote(chat.id, videoNote, duration, length, notFalse(disableNotification), replyToMessageId, replyMarkup).call
          .asInstanceOf[F[M]]

      case VoiceContent(voice, caption, parseMode, duration) =>
        SendVoice(chat.id,
                  voice,
                  nonEmpty(caption),
                  parseMode,
                  duration,
                  notFalse(disableNotification),
                  replyToMessageId,
                  replyMarkup).call.asInstanceOf[F[M]]
    }

  /**
    * Sends a list of media files as an album
    * @param media Must include 2-10 items
    */
  def sendAlbum(media: List[InputMedia], disableNotification: Boolean = false): F[List[TelegramMessage]] =
    SendMediaGroup(chat.id, media, disableNotification = Some(disableNotification)).call

}
