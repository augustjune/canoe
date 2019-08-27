package canoe.methods.webhooks

import canoe.marshalling.{CirceDecoders, CirceEncoders}
import canoe.methods.Method
import canoe.models.{InputFile, WebhookInfo}
import io.circe.{Decoder, Encoder}

/** Use this method to get current webhook status.
  * Requires no parameters. On success, returns a WebhookInfo object.
  * If the bot is using getUpdates, will return an object with the url field empty.
  */
case object GetWebhookInfo {

  implicit val method: Method[GetWebhookInfo.type, WebhookInfo] =
    new Method[GetWebhookInfo.type, WebhookInfo] {

      def name: String = "getWebhookInfo"

      def encoder: Encoder[GetWebhookInfo.type] = CirceEncoders.getWebhookInfoEncoder

      def decoder: Decoder[WebhookInfo] = CirceDecoders.webhookInfoDecoder

      def uploads(request: GetWebhookInfo.type): List[(String, InputFile)] = Nil
    }
}
