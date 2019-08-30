package canoe.methods.webhooks

import canoe.methods.Method
import canoe.models.{InputFile, WebhookInfo}
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder, Json}

/** Use this method to get current webhook status.
  * Requires no parameters. On success, returns a WebhookInfo object.
  * If the bot is using getUpdates, will return an object with the url field empty.
  */
case object GetWebhookInfo {

  implicit val method: Method[GetWebhookInfo.type, WebhookInfo] =
    new Method[GetWebhookInfo.type, WebhookInfo] {

      def name: String = "getWebhookInfo"

      def encoder: Encoder[GetWebhookInfo.type] = Encoder.instance(_ => Json.Null)

      def decoder: Decoder[WebhookInfo] = deriveDecoder[WebhookInfo]

      def uploads(request: GetWebhookInfo.type): List[(String, InputFile)] = Nil
    }
}
