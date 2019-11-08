package canoe.models.messages

import canoe.models.{Animation, Chat, MessageEntity, User}

final case class AnimationMessage(messageId: Int,
                                  chat: Chat,
                                  date: Int,
                                  animation: Animation,
                                  caption: Option[String] = None,
                                  captionEntities: Option[List[MessageEntity]] = None,
                                  from: Option[User] = None,
                                  forwardFrom: Option[User] = None,
                                  forwardFromChat: Option[Chat] = None,
                                  forwardFromMessageId: Option[Int] = None,
                                  forwardSignature: Option[String] = None,
                                  forwardSenderName: Option[String] = None,
                                  forwardDate: Option[Int] = None,
                                  replyToMessage: Option[TelegramMessage] = None,
                                  editDate: Option[Int] = None,
                                  authorSignature: Option[String] = None)
    extends UserMessage
