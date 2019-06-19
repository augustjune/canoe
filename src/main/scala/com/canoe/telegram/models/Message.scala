package com.canoe.telegram.models


//sealed trait Message1 {
//  def messageId: Int
//  def chat: Chat
//  def date: Int
//}
//
//case class AnimationMessage(messageId: Int, chat: Chat, date: Int,
//                            animation: Animation, caption: String, captionEntities: Seq[MessageEntity],
//                            from: Option[User] = None,
//                            forwardFrom: Option[User] = None,
//                            forwardFromChat: Option[Chat] = None,
//                            forwardFromMessageId: Option[Int] = None,
//                            forwardSignature: Option[String] = None,
//                            forwardDate: Option[Int] = None,
//                            replyToMessage: Option[Message1] = None,
//                            editDate: Option[Int] = None,
//                            authorSignature: Option[String] = None
//                           ) extends Message1
//
//case class AudioMessage(messageId: Int, chat: Chat, date: Int,
//                        audio: Audio, caption: String, captionEntities: Seq[MessageEntity]
//                       ) extends Message1
//
//case class DocumentMessage(messageId: Int, chat: Chat, date: Int,
//                           document: Document, caption: String, captionEntities: Seq[MessageEntity]
//                          ) extends Message1
//
//case class GameMessage(messageId: Int, chat: Chat, date: Int,
//                       game: Game) extends Message1
//
//case class TextMessage(messageId: Int, chat: Chat, date: Int,
//                       text: String, entities: Seq[MessageEntity]
//                      ) extends Message1
//
//case class StickerMessage(messageId: Int, chat: Chat, date: Int,
//                          sticker: Sticker) extends Message1
//
//case class VideoMessage(messageId: Int, chat: Chat, date: Int,
//                        video: Video, caption: String, captionEntities: Seq[MessageEntity]
//                       ) extends Message1
//
//case class VideoNoteMessage(messageId: Int, chat: Chat, date: Int,
//                            videoNote: VideoNote) extends Message1
//
//case class VoiceMessage(messageId: Int, chat: Chat, date: Int,
//                        voice: Voice, caption: String, captionEntities: Seq[MessageEntity]
//                       ) extends Message1
//
//case class ContactMessage(messageId: Int, chat: Chat, date: Int,
//                          contact: Contact) extends Message1
//
//case class LocationMessage(messageId: Int, chat: Chat, date: Int,
//                           location: Location) extends Message1
//
//case class VenueMessage(messageId: Int, chat: Chat, date: Int,
//                        venue: Venue) extends Message1
//
//case class PollMessage(messageId: Int, chat: Chat, date: Int,
//                       poll: Poll) extends Message1
//
//case class InvoiceMessage(messageId: Int, chat: Chat, date: Int,
//                          invoiceMessage: InvoiceMessage) extends Message1
//
//
//
//case class MessagePinned(messageId: Int, chat: Chat, date: Int,
//                         message: Message1) extends Message1
//
//case class ChatMemberAdded(messageId: Int, chat: Chat, date: Int,
//                           newChatMembers: Seq[User]) extends Message1
//
//case class ChatMemberLeft(messageId: Int, chat: Chat, date: Int,
//                          leftChatMember: User) extends Message1
//
//case class ChatTitleChanged(messageId: Int, chat: Chat, date: Int,
//                            newChatTitle: String) extends Message1
//
//case class ChatPhotoChanged(messageId: Int, chat: Chat, date: Int,
//                            newChatPhoto: Seq[PhotoSize]) extends Message1
//
//case class ChatPhotoDeleted(messageId: Int, chat: Chat, date: Int,
//                            deleteChatPhoto: Boolean) extends Message1
//
//case class SupergroupCreated(messageId: Int, chat: Chat, date: Int,
//                             supergroupChatCreated: Boolean) extends Message1
//
//case class ChannelCreated(messageId: Int, chat: Chat, date: Int,
//                          channelChatCreated: Boolean) extends Message1
//
//case class MigratedToSupergroup(messageId: Int, chat: Chat, date: Int,
//                                migrateToChatId: Long) extends Message1
//
//case class MigratedFromGroup(messageId: Int, chat: Chat, date: Int,
//                             migrateFromChatId: Long) extends Message1
//
//case class SuccessfulPaymentMessage(messageId: Int, chat: Chat, date: Int,
//                                    successfulPayment: SuccessfulPayment) extends Message1
//
//case class WebsiteConnected(messageId: Int, chat: Chat, date: Int,
//                            connectedWebsite: String) extends Message1
//

//case class Message(messageId: Int,
//                    date: Int,
//                    chat: Chat,
//                    from: Option[User] = None,
//                    forwardFrom: Option[User] = None,
//                    forwardFromChat: Option[Chat] = None,
//                    forwardFromMessageId: Option[Int] = None,
//                    forwardSignature: Option[String] = None,
//                    forwardDate: Option[Int] = None,
//                    replyToMessage: Option[Message] = None,
//                    editDate: Option[Int] = None,
//                    authorSignature: Option[String] = None,
//
//                    text: Option[String] = None,
//                    entities: Option[Seq[MessageEntity]] = None,
//                    captionEntities: Option[Array[MessageEntity]] = None,
//                    photo: Option[Seq[PhotoSize]] = None,
//                    sticker: Option[Sticker] = None,
//                    video: Option[Video] = None,
//                    voice: Option[Voice] = None,
//                    videoNote: Option[VideoNote] = None,
//                    newChatMembers: Option[Array[User]] = None,
//                    caption: Option[String] = None,
//                    contact: Option[Contact] = None,
//                    location: Option[Location] = None,
//                    venue: Option[Venue] = None,
//
//                    leftChatMember: Option[User] = None,
//                    newChatTitle: Option[String] = None,
//                    newChatPhoto: Option[Seq[PhotoSize]] = None,
//                    deleteChatPhoto: Option[Boolean] = None,
//                    groupChatCreated: Option[Boolean] = None,
//                    supergroupChatCreated: Option[Boolean] = None,
//                    channelChatCreated: Option[Boolean] = None,
//                    migrateToChatId: Option[Long] = None,
//                    migrateFromChatId: Option[Long] = None,
//                    pinnedMessage: Option[Message] = None,
//                    invoice: Option[Invoice] = None,
//                    successfulPayment: Option[SuccessfulPayment] = None,
//                    connectedWebsite: Option[String] = None
//                  ) {
//
//  def source: Long = chat.id // ChatId.Chat(chat.id)
//}

