package canoe.api.models

import canoe.clients.TelegramClient
import canoe.methods.chats._
import canoe.methods.messages._
import canoe.models.ChatAction.ChatAction
import canoe.models._
import canoe.models.messages.TelegramMessage
import canoe.models.outgoing._
import cats.Applicative

final class ChatApi[F[_]](chat: Chat)(implicit client: TelegramClient[F]) {

  def setAction(action: ChatAction): F[Boolean] =
    client.execute(SendChatAction(chat.id, action))

  def setDefaultPermissions(permissions: ChatPermissions): F[Boolean] =
    client.execute(SetChatPermissions(chat.id, permissions))

  def deletePhoto: F[Boolean] =
    client.execute(DeleteChatPhoto(chat.id))

  def deleteStickerSet: F[Boolean] =
    client.execute(DeleteChatStickerSet(chat.id))

  def exportInviteLink: F[String] =
    client.execute(ExportChatInviteLink(chat.id))

  def administrators(implicit F: Applicative[F]): F[List[ChatMember]] =
    chat match {
      case _: PrivateChat => F.pure(Nil)
      case _              => client.execute(GetChatAdministrators(chat.id))
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

  def restrictMember(user: User, permissions: ChatPermissions, until: Option[Int] = None): F[Boolean] =
    client.execute(RestrictChatMember(chat.id, user.id, permissions, until))

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

  private def nonEmpty(str: String): Option[String] =
    if (str.isEmpty) None
    else Some(str)

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
