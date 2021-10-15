package canoe.methods.messages

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.ParseMode.ParseMode
import canoe.models.messages.PollMessage
import canoe.models.{ChatId, InputFile, ReplyMarkup}
import io.circe.generic.semiauto
import io.circe.{Decoder, Encoder}

/**
  * Use this method to send a native poll.
  * A native poll can't be sent to a private chat. On success, the sent Message is returned.
  *
  * @param chatId                Unique identifier for the target chat or username of the target channel
  *                              (in the format @channelusername)
  * @param question              Poll question, 1-255 characters
  * @param options               List of answer options, 2-10 strings 1-100 characters each
  * @param isAnonymous           True, if the poll needs to be anonymous, defaults to True.
  * @param `type`                Poll type, “quiz” or “regular”, defaults to “regular”.
  * @param allowsMultipleAnswers True, if the poll allows multiple answers, ignored for polls in quiz mode, defaults to False.
  * @param correctOptionId       0-based identifier of the correct answer option, required for polls in quiz mode.
  * @param explanation           Text that is shown when a user chooses an incorrect answer or taps on the lamp icon in a quiz-style poll,
  *                              0-200 characters with at most 2 line feeds after entities parsing.
  * @param explanationParseMode  Mode for parsing entities in the explanation.
  * @param openPeriod            Amount of time in seconds the poll will be active after creation, 5-600.
  *                              Can't be used together with close_date.
  * @param closeDate             Point in time (Unix timestamp) when the poll will be automatically closed.
  *                              Must be at least 5 and no more than 600 seconds in the future.
  *                              Can't be used together with open_period.
  * @param isClosed              Pass True, if the poll needs to be immediately closed.
  *                              This can be useful for poll preview.
  * @param disableNotification   Sends the message silently.
  *                              iOS users will not receive a notification,
  *                              Android users will receive a notification with no sound
  * @param replyToMessageId      If the message is a reply, ID of the original message
  * @param replyMarkup           Additional interface options.
  *                              A JSON-serialized object for an inline keyboard, custom reply keyboard,
  *                              instructions to hide reply keyboard or to force a reply from the user.
  */
final case class SendPoll(chatId: ChatId,
                          question: String,
                          options: List[String],
                          isAnonymous: Option[Boolean] = None,
                          `type`: Option[String] = None,
                          allowsMultipleAnswers: Option[Boolean] = None,
                          correctOptionId: Option[Int] = None,
                          explanation: Option[String],
                          explanationParseMode: Option[ParseMode],
                          openPeriod: Option[Int],
                          closeDate: Option[Int],
                          isClosed: Option[Boolean] = None,
                          disableNotification: Option[Boolean] = None,
                          replyToMessageId: Option[Int] = None,
                          replyMarkup: Option[ReplyMarkup] = None
)

object SendPoll {
  import io.circe.generic.auto._

  implicit val method: Method[SendPoll, PollMessage] =
    new Method[SendPoll, PollMessage] {

      def name: String = "sendPoll"

      def encoder: Encoder[SendPoll] = semiauto.deriveEncoder[SendPoll].snakeCase

      def decoder: Decoder[PollMessage] = semiauto.deriveDecoder[PollMessage]

      def attachments(request: SendPoll): List[(String, InputFile)] = Nil
    }
}
