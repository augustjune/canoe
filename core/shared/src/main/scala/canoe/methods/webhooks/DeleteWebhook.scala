package canoe.methods.webhooks

import canoe.methods.Method
import canoe.models.InputFile
import io.circe.{Decoder, Encoder, Json}

/** Use this method to remove webhook integration if you decide to switch back to getUpdates.
  * Returns True on success. Requires no parameters.
  */
case object DeleteWebhook {

  implicit val method: Method[DeleteWebhook.type, Boolean] =
    new Method[DeleteWebhook.type, Boolean] {

      def name: String = "deleteWebhook"

      def encoder: Encoder[DeleteWebhook.type] = Encoder.instance(_ => Json.Null)

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def attachments(request: DeleteWebhook.type): List[(String, InputFile)] = Nil
    }
}
