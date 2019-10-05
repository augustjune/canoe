package canoe.methods.messages

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.Currency.Currency
import canoe.models.messages.TelegramMessage
import canoe.models.{InputFile, LabeledPrice, ReplyMarkup}
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/**
  * Use this method to send invoices.
  * On success, the sent Message is returned.
  *
  * @param chatId              Unique identifier for the target private chat
  * @param title               Product name
  * @param description         Product description
  * @param payload             Bot-defined invoice payload, 1-128 bytes. This will not be displayed to the user, use for your internal processes.
  * @param providerToken       Payments provider token, obtained via Botfather
  * @param startParameter      Unique deep-linking parameter that can be used to generate this invoice when used as a start parameter
  * @param currency            Three-letter ISO 4217 currency code, see more on currencies
  * @param prices              Price breakdown, a list of components (e.g. product price, tax, discount, delivery cost, delivery tax, bonus, etc.)
  * @param providerData        JSON-encoded data about the invoice, which will be shared with the payment provider.
  *                            A detailed description of required fields should be provided by the payment provider.
  * @param photoUrl            URL of the product photo for the invoice.
  *                            Can be a photo of the goods or a marketing image for a service.
  *                            People like it better when they see what they are paying for.
  * @param photoSize           Photo size
  * @param photoWidth          Photo width
  * @param photoHeight         Photo height
  * @param needName            Pass True, if you require the user's full name to complete the order
  * @param needPhoneNumber     Pass True, if you require the user's phone number to complete the order
  * @param needEmail           Pass True, if you require the user's email to complete the order
  * @param needShippingAddress Pass True, if you require the user's shipping address to complete the order
  * @param isFlexible          Pass True, if the final price depends on the shipping method
  * @param disableNotification Sends the message silently. Users will receive a notification with no sound.
  * @param replyToMessageId    If the message is a reply, ID of the original message
  * @param replyMarkup         A JSON-serialized object for an inline keyboard.
  *                            If empty, one 'Pay total price' button will be shown.
  *                            If not empty, the first button must be a Pay button.
  */
final case class SendInvoice(chatId: Long,
                             title: String,
                             description: String,
                             payload: String,
                             providerToken: String,
                             startParameter: String,
                             currency: Currency,
                             prices: Array[LabeledPrice],
                             providerData: Option[String] = None,
                             photoUrl: Option[String] = None,
                             photoSize: Option[Int] = None,
                             photoWidth: Option[Int] = None,
                             photoHeight: Option[Int] = None,
                             needName: Option[Boolean] = None,
                             needPhoneNumber: Option[Boolean] = None,
                             needEmail: Option[Boolean] = None,
                             needShippingAddress: Option[Boolean] = None,
                             isFlexible: Option[Boolean] = None,
                             disableNotification: Option[Boolean] = None,
                             replyToMessageId: Option[Int] = None,
                             replyMarkup: Option[ReplyMarkup] = None)

object SendInvoice {
  import io.circe.generic.auto._

  implicit val method: Method[SendInvoice, TelegramMessage] =
    new Method[SendInvoice, TelegramMessage] {

      def name: String = "sendInvoice"

      def encoder: Encoder[SendInvoice] = deriveEncoder[SendInvoice].snakeCase

      def decoder: Decoder[TelegramMessage] = TelegramMessage.telegramMessageDecoder

      def uploads(request: SendInvoice): List[(String, InputFile)] = Nil
    }
}
