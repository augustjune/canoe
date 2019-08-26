package canoe.methods.messages

import canoe.methods.MultipartRequest
import canoe.models.messages.TelegramMessage
import canoe.models.{ChatId, InputFile, InputMedia}

/**
  * Use this method to send a group of photos or videos as an album.
  * On success, an array of the sent Messages is returned.
  *
  * @param chatId              Integer or String Unique identifier for the target chat or username of the target channel (in the format @channelusername)
  * @param media               Array of InputMedia A JSON-serialized array describing photos and videos to be sent, must include 2–10 items
  * @param disableNotification Boolean Optional Sends the messages silently. Users will receive a notification with no sound.
  * @param replyToMessageId    Integer Optional If the messages are a reply, ID of the original message
  */
case class SendMediaGroup(chatId: ChatId,
                          media: List[InputMedia],
                          disableNotification: Option[Boolean] = None,
                          replyToMessageId: Option[Int] = None) extends MultipartRequest[List[TelegramMessage]] {

  override def getFiles: List[(String, InputFile)] = media.flatMap(_.files)
}
