package canoe.api.sources

import canoe.api.TelegramClient
import canoe.models.{InputFile, Update}
import cats.effect.Resource
import cats.effect.std.Queue
import fs2.Stream
import javax.naming.OperationNotSupportedException

class Hook[F[_]](queue: Queue[F, Update]) {
  def updates: Stream[F, Update] = Stream.repeatEval(queue.take)
}

object Hook {

  /** Installs a webhook for Telegram updates to be sent to the specified `url`
    * and starts a local server which listen to incoming updates on specified `port`.
    *
    * After the hook is used, local server and Telegram webhook are cleaned up.
    *
    * @param url         HTTPS url to which updates will be sent
    * @param host        Network interface to bind the server
    * @param port        Port which will be used for listening for the incoming updates
    * @param certificate Public key of self-signed certificate (including BEGIN and END portions)
    */
  def install[F[_]: TelegramClient](
    url: String,
    host: String,
    port: Int,
    certificate: Option[InputFile]
  ): Resource[F, Hook[F]] =
    throw new OperationNotSupportedException("Webhook is not supported for JS version.")
}
