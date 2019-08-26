package canoe.methods.webhooks

import canoe.marshalling.CirceEncoders
import canoe.methods.{JsonRequest, Method}
import canoe.models.InputFile
import io.circe.{Decoder, Encoder}

/** Use this method to remove webhook integration if you decide to switch back to getUpdates.
  * Returns True on success. Requires no parameters.
  */
case object DeleteWebhook extends JsonRequest[Boolean] {

  implicit val method: Method[DeleteWebhook.type, Boolean] =
    new Method[DeleteWebhook.type, Boolean] {

      def name: String = "deleteWebhook"

      def encoder: Encoder[DeleteWebhook.type] = CirceEncoders.deleteWebhookEncoder

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def uploads(request: DeleteWebhook.type): List[(String, InputFile)] = Nil
    }
}
