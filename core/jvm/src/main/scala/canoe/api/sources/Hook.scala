package canoe.api.sources

import canoe.api.{TelegramClient}
import canoe.methods.webhooks.{DeleteWebhook, SetWebhook}
import canoe.models.{InputFile, Update}
import canoe.syntax.methodOps
import cats.Monad
import cats.effect.{ConcurrentEffect, Resource, Timer}
import cats.syntax.all._
import fs2.Stream
import fs2.concurrent.Queue
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s._
import org.http4s.circe.jsonOf
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.http4s.server.Server
import org.http4s.server.blaze.BlazeServerBuilder

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
    Resource.suspend(Slf4jLogger.create.map { implicit logger =>
      for {
        _    <- setTelegramWebhook(url, certificate)
        hook <- listenServer[F](port)
      } yield hook
    })

  /**
    * Sets updates webhook to provided `url` for Telegram service.
    * After the resource is used, webhook will be automatically deleted.
    *
    * @param url         URL to which updates will be sent
    * @param certificate Public key of self-signed certificate (including BEGIN and END portions)
    */
  private def setTelegramWebhook[F[_]: TelegramClient: Monad: Logger](
    url: String,
    certificate: Option[InputFile]
  ): Resource[F, Unit] =
    Resource.make(
      F.info(
        "Setting a webhook to the Telegram service. Don't forget to delete the webhook, since it blocks you from using polling methods."
      ) *> SetWebhook(url, certificate).call.void
    )(
      _ =>
        F.info(
          "Telegram webhook is deleted. Polling is available again."
        ) *> DeleteWebhook.call.void
    )

  /**
    * Creates local server which listens for the incoming updates on provided `port`.
    */
  private def listenServer[F[_]: ConcurrentEffect: Timer: Logger](port: Int): Resource[F, Hook[F]] = {
    val dsl = Http4sDsl[F]
    import dsl._

    def app(queue: Queue[F, Update]): HttpApp[F] =
      HttpRoutes
        .of[F] {
          case req @ POST -> Root =>
            req
              .decodeWith(jsonOf[F, Update], strict = true)(queue.enqueue1(_) *> Ok())
              .recoverWith {
                case InvalidMessageBodyFailure(details, _) =>
                  F.error(s"Received unknown type of update. $details") *> Ok()
              }
        }
        .orNotFound

    def server(queue: Queue[F, Update]): Resource[F, Server[F]] =
      BlazeServerBuilder[F].bindHttp(port).withHttpApp(app(queue)).resource

    Resource.suspend(Queue.unbounded[F, Update].map(q => server(q).map(_ => new Hook[F](q))))
  }
}
