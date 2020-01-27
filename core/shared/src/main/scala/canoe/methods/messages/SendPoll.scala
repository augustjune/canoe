package canoe.methods.messages

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.messages.PollMessage
import canoe.models.{ChatId, InputFile, ReplyMarkup}
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}

/**
  * Use this method to send a native poll.
  * A native poll can't be sent to a private chat. On success, the sent Message is returned.
  *
  * @param chatId              Unique identifier for the target chat or username of the target channel
  *                            (in the format @channelusername)
  * @param question            Poll question, 1-255 characters
  * @param options             List of answer options, 2-10 strings 1-100 characters each
  * @param disableNotification Sends the message silently.
  *                            iOS users will not receive a notification,
  *                            Android users will receive a notification with no sound
  * @param replyToMessageId    If the message is a reply, ID of the original message
  * @param replyMarkup         Additional interface options.
  *                            A JSON-serialized object for an inline keyboard, custom reply keyboard,
  *                            instructions to hide reply keyboard or to force a reply from the user.
  */
final case class SendPoll(chatId: ChatId,
                          question: String,
                          options: List[String],
                          isAnonymous: Option[Boolean] = None,
                          `type`: Option[String] = None,
                          allowsMultipleAnswers: Option[Boolean] = None,
                          correctOptionId: Option[Int] = None,
                          isClosed: Option[Boolean] = None,
                          disableNotification: Option[Boolean] = None,
                          replyToMessageId: Option[Int] = None,
                          replyMarkup: Option[ReplyMarkup] = None)

object SendPoll {
  import io.circe.generic.auto._

  implicit val method: Method[SendPoll, PollMessage] =
    new Method[SendPoll, PollMessage] {

      def name: String = "sendPoll"

      def encoder: Encoder[SendPoll] = deriveEncoder[SendPoll].snakeCase

      def decoder: Decoder[PollMessage] = deriveDecoder[PollMessage]

      def attachments(request: SendPoll): List[(String, InputFile)] = Nil
    }
}
