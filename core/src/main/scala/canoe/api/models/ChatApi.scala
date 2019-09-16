package canoe.api.models

import canoe.api.TelegramClient
import canoe.methods.chats._
import canoe.methods.messages._
import canoe.models.ChatAction.ChatAction
import canoe.models._
import canoe.models.messages.TelegramMessage
import canoe.models.outgoing._
import cats.Applicative

final class ChatApi[F[_]](chat: Chat)(implicit client: TelegramClient[F]) {

  /**
    * Sets the status of the bot for this chat
    *
    * The action is set for 5 seconds or until
    * the first message is send by the bot
    */
  def setAction(action: ChatAction): F[Boolean] =
    client.execute(SendChatAction(chat.id, action))

  /**
    * Sets default permissions for all chat members
    *
    * The bot must be an administrator in the group or a supergroup
    * and must have the can_restrict_members admin rights
    */
  def setDefaultPermissions(permissions: ChatPermissions)(implicit F: Applicative[F]): F[Boolean] =
    chat match {
      case _: Group | _: Supergroup =>
        client.execute(SetChatPermissions(chat.id, permissions))
      case _ => F.pure(false)
    }

  /**
    * Deletes the photo of the this chat
    */
  def deletePhoto: F[Boolean] =
    client.execute(DeleteChatPhoto(chat.id))

  /**
    * Deletes a group of stickers set for this chat
    */
  def deleteStickerSet: F[Boolean] =
    client.execute(DeleteChatStickerSet(chat.id))

  /**
    * Generates new invite link ofr this chat.
    *
    * Any previously generated link is revoked
    */
  def exportInviteLink: F[String] =
    client.execute(ExportChatInviteLink(chat.id))

  /**
    * @return list of administrators of this chat
    */
  def administrators(implicit F: Applicative[F]): F[List[ChatMember]] =
    chat match {
      case _: PrivateChat => F.pure(Nil)
      case _              => client.execute(GetChatAdministrators(chat.id))
    }

  /**
    * @return Detailed information about the member of this chat
    */
  def getMember(user: User): F[ChatMember] =
    client.execute(GetChatMember(chat.id, user.id))

  /**
    * @return number of members of this chat
    */
  def membersCount: F[Int] =
    client.execute(GetChatMembersCount(chat.id))

  // ToDo - add parameters
  /**
    * Kicks a user from this chat
    */
  def kick(user: User)(implicit F: Applicative[F]): F[Boolean] =
    chat match {
      case _: PrivateChat => F.pure(false)
      case _ => client.execute(KickChatMember(chat.id, user.id))
    }

  /**
    * Abandons this chat
    */
  def leave(implicit F: Applicative[F]): F[Boolean] =
    chat match {
      case _: PrivateChat => F.pure(false)
      case _ => client.execute(LeaveChat(chat.id))
    }

  // ToDo - add parameters
  /**
    * Pins message in this chat, unless it's private chat
    */
  def pinMessage(message: TelegramMessage)(implicit F: Applicative[F]): F[Boolean] =
    chat match {
      case _: PrivateChat => F.pure(false)
      case _ => client.execute(PinChatMessage(chat.id, message.messageId))
    }

  // ToDo - add parameters
  /**
    * Promotes or demotes a user in this chat
    * @return
    */
  def promoteMember(user: User)(implicit F: Applicative[F]): F[Boolean] =
    chat match {
      case _: Supergroup | _: Channel =>
        client.execute(PromoteChatMember(chat.id, user.id))

      case _ => F.pure(false)
    }

  /**
    * Restricts a user in a supergroup
    *
    * The bot must be an administrator in the supergroup
    * and must have the appropriate admin rights
    */
  def restrictMember(user: User, permissions: ChatPermissions, until: Option[Int] = None)(
    implicit F: Applicative[F]
  ): F[Boolean] =
    chat match {
      case _: Supergroup =>
        client.execute(RestrictChatMember(chat.id, user.id, permissions, until))

      case _ => F.pure(false)
    }

  /**
    * Changes the description of this chat if it's a group, a supergroup or a channel
    */
  def setDescription(description: String)(implicit F: Applicative[F]): F[Boolean] =
    chat match {
      case _: Group | _: Supergroup | _: Channel =>
        client.execute(SetChatDescription(chat.id, Option(description)))

      case _ => F.pure(false)
    }

  /**
    * Changes the title of this chat, unless it's private chat
    */
  def setTitle(title: String)(implicit F: Applicative[F]): F[Boolean] =
    chat match {
      case _: PrivateChat => F.pure(false)
      case _ => client.execute(SetChatTitle(chat.id, title))
    }

  /**
    * Unbans previously kicked user
    */
  def unbanMember(user: User): F[Boolean] =
    client.execute(UnbanChatMember(chat.id, user.id))

  /**
    * Unpins pinned chat message
    */
  def unpinMessage(implicit F: Applicative[F]): F[Boolean] =
    chat match {
      case _: PrivateChat => F.pure(false)
      case _ => client.execute(UnpinChatMessage(chat.id))
    }

  /**
    * @return Detailed information about this chat
    */
  def details: F[DetailedChat] =
    client.execute(GetChat(chat.id))

  private def nonEmpty(str: String): Option[String] =
    if (str.isEmpty) None
    else Some(str)

  /**
    * Sends a message to this chat
    */
  def send(content: MessageContent,
           replyToMessageId: Option[Int] = None,
           replyMarkup: Option[ReplyMarkup] = None,
           disableNotification: Option[Boolean] = None): F[TelegramMessage] =
    content match {
      case AnimationContent(animation, caption, duration, width, height, thumb, parseMode) =>
        client.execute(
          SendAnimation(chat.id,
                        animation,
                        duration,
                        width,
                        height,
                        thumb,
                        nonEmpty(caption),
                        parseMode,
                        disableNotification,
                        replyToMessageId,
                        replyMarkup)
        )

      case AudioContent(audio, caption, duration, parseMode, performer, title) =>
        client.execute(
          SendAudio(chat.id,
                    audio,
                    duration,
                    nonEmpty(caption),
                    parseMode,
                    performer,
                    title,
                    disableNotification,
                    replyToMessageId,
                    replyMarkup)
        )

      case ContactContent(phoneNumber, firstName, lastName, vcard) =>
        client.execute(
          SendContact(chat.id,
                      phoneNumber,
                      firstName,
                      lastName,
                      vcard,
                      disableNotification,
                      replyToMessageId,
                      replyMarkup)
        )

      case DocumentContent(document, caption, parseMode) =>
        client.execute(
          SendDocument(chat.id,
                       document,
                       nonEmpty(caption),
                       parseMode,
                       disableNotification,
                       replyToMessageId,
                       replyMarkup)
        )

      case GameContent(gameShortName) =>
        client.execute(SendGame(chat.id, gameShortName, disableNotification, replyToMessageId, replyMarkup))

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
        client.execute(
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
            disableNotification,
            replyToMessageId,
            replyMarkup
          )
        )

      case LocationContent(latitude, longitude, livePeriod) =>
        client.execute(
          SendLocation(chat.id, latitude, longitude, livePeriod, disableNotification, replyToMessageId, replyMarkup)
        )

      case TextContent(text, parseMode, disableWebPagePreview) =>
        client.execute(
          SendMessage(chat.id,
                      text,
                      parseMode,
                      disableWebPagePreview,
                      disableNotification,
                      replyToMessageId,
                      replyMarkup)
        )

      case PhotoContent(photo, caption, parseMode) =>
        client.execute(
          SendPhoto(chat.id, photo, nonEmpty(caption), parseMode, disableNotification, replyToMessageId, replyMarkup)
        )

      case PollContent(question, options) =>
        client.execute(SendPoll(chat.id, question, options, disableNotification, replyToMessageId, replyMarkup))

      case StickerContent(sticker) =>
        client.execute(SendSticker(chat.id, sticker, disableNotification, replyToMessageId, replyMarkup))

      case VenueContent(latitude, longitude, title, address, foursquareId, foursquareType, duration) =>
        client.execute(
          SendVenue(chat.id,
                    latitude,
                    longitude,
                    title,
                    address,
                    foursquareId,
                    foursquareType,
                    duration,
                    disableNotification,
                    replyToMessageId,
                    replyMarkup)
        )

      case VideoContent(video, caption, duration, width, height, parseMode, supportsStreaming) =>
        client.execute(
          SendVideo(chat.id,
                    video,
                    duration,
                    width,
                    height,
                    nonEmpty(caption),
                    parseMode,
                    supportsStreaming,
                    disableNotification,
                    replyToMessageId,
                    replyMarkup)
        )

      case VideoNoteContent(videoNote, duration, length) =>
        client.execute(
          SendVideoNote(chat.id, videoNote, duration, length, disableNotification, replyToMessageId, replyMarkup)
        )

      case VoiceContent(voice, caption, parseMode, duration) =>
        client.execute(
          SendVoice(chat.id,
                    voice,
                    nonEmpty(caption),
                    parseMode,
                    duration,
                    disableNotification,
                    replyToMessageId,
                    replyMarkup)
        )
    }

  /**
    * Sends a list of media files as an album
    * @param media Must include 2-10 items
    */
  def sendAlbum(media: List[InputMedia], disableNotification: Boolean = false): F[List[TelegramMessage]] =
    client.execute(SendMediaGroup(chat.id, media, disableNotification = Some(disableNotification)))

}
