package canoe.methods.webhooks

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.InputFile
import canoe.models.UpdateType.UpdateType
import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, Encoder}

/**
  * Use this method to specify a url and receive incoming updates via an outgoing webhook.
  *
  * Whenever there is an update for the bot, we will send an HTTPS POST request to the specified url,
  * containing a JSON-serialized Update.
  * In case of an unsuccessful request, we will give up after a reasonable amount of attempts.
  * Returns true.
  *
  * If you'd like to make sure that the Webhook request comes from Telegram,
  * we recommend using a secret path in the URL, e.g. https://www.example.com/<token>.
  * Since nobody else knows your bot‘s token, you can be pretty sure it’s us.
  *
  * If you're having any trouble setting up webhooks,
  * please check out this [[https://core.telegram.org/bots/webhooks amazing guide to Webhooks]].
  *
  * '''Notes'''
  *   1. You will not be able to receive updates using getUpdates for as long as an outgoing webhook is set up.
  *   2. To use a self-signed certificate, you need to upload your public key certificate using certificate parameter.
  *   3. Ports currently supported for Webhooks: 443, 80, 88, 8443.
  *
  *
  * @param url            HTTPS url to send updates to. Use an empty string to remove webhook integration
  * @param certificate    Upload your public key certificate so that the root certificate in use can be checked.
  *                       For details, see Telegram self-signed [[https://core.telegram.org/bots/self-signed guide]]
  * @param maxConnections Maximum allowed number of simultaneous HTTPS connections to the webhook for update delivery, 1-100.
  *                       Defaults to 40.
  *                       Use lower values to limit the load on your bot‘s server, and higher values to increase your bot’s throughput.
  * @param allowedUpdates List the types of updates you want your bot to receive.
  *                       Specify an empty list to receive all updates regardless of type (default).
  *                       If not specified, the previous setting will be used.
  *                       Please note that this parameter doesn't affect updates created before the call to the setWebhook,
  *                       so unwanted updates may be received for a short period of time.
  *
  */
final case class SetWebhook(url: String,
                            certificate: Option[InputFile] = None,
                            maxConnections: Option[Int] = None,
                            allowedUpdates: Option[Seq[UpdateType]] = None)

object SetWebhook {

  implicit val method: Method[SetWebhook, Boolean] =
    new Method[SetWebhook, Boolean] {

      def name: String = "setWebhook"

      def encoder: Encoder[SetWebhook] = deriveEncoder[SetWebhook].snakeCase

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def attachments(request: SetWebhook): List[(String, InputFile)] =
        request.certificate.map("certificate" -> _).toList
    }
}
