package canoe.api.sources

import canoe.api.TelegramClient
import canoe.models.{InputFile, Update}
import cats.effect.{ConcurrentEffect, Resource, Timer}
import fs2.Stream
import fs2.concurrent.Queue
import javax.naming.OperationNotSupportedException

class Hook[F[_]](queue: Queue[F, Update]) {
  def updates: Stream[F, Update] = queue.dequeue
}

object Hook {

  /**
    * Installs a webhook for Telegram updates to be sent to the specified `url`
    * and starts a local server which listen to incoming updates on specified `port`.
    *
    * After the hook is used, local server and Telegram webhook are cleaned up.
    *
    * @param url         HTTPS url to which updates will be sent
    * @param port        Port which will be used for listening for the incoming updates
    * @param certificate Public key of self-signed certificate (including BEGIN and END portions)
    */
  def install[F[_]: TelegramClient: ConcurrentEffect: Timer](url: String,
                                                             port: Int,
                                                             certificate: Option[InputFile]): Resource[F, Hook[F]] =
    throw new OperationNotSupportedException("Webhook is not supported for JS version.")
}
