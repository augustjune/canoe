package canoe.api

import canoe.api.clients.Http4sTelegramClient
import canoe.methods.Method
import cats.effect.{ConcurrentEffect, Resource, Sync}
import io.chrisdavenport.log4cats.Logger
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder

import scala.concurrent.ExecutionContext

/**
  * Client which is able to execute Telegram Bot API methods in effect `F`.
  */
trait TelegramClient[F[_]] {

  /**
    * Transforms request into result using implicit method definition as a contract.
    */
  def execute[Req, Res](request: Req)(implicit M: Method[Req, Res]): F[Res]
}

object TelegramClient {

  private implicit def defaultLogger[F[_]: Sync]: Logger[F] = Slf4jLogger.getLogger[F]

  /**
    * Creates an authorized asynchronous Telegram Bot API client wrapped in Resource.
    * After it is used, client is going to be released.
    *
    * @param token Telegram bot token
    * @param ec Dedicated ExecutionContext
    */
  def apply[F[_]: ConcurrentEffect](token: String, ec: ExecutionContext): Resource[F, TelegramClient[F]] =
    BlazeClientBuilder[F](ec).resource.map(new Http4sTelegramClient[F](token, _))

  /**
    * Creates an authorized asynchronous Telegram Bot API client wrapped in Resource,
    * which works on `global` ExecutionContext
    *
    * @param token Telegram bot token
    */
  def global[F[_]: ConcurrentEffect](token: String): Resource[F, TelegramClient[F]] =
    apply(token, scala.concurrent.ExecutionContext.global)

  /**
    * Creates an authorized asynchronous Telegram Bot API out of http4s Client.
    *
    * @param token Telegram bot token
    */
  def fromHttp4sClient[F[_]: Sync](token: String)(client: Client[F]): TelegramClient[F] =
    new Http4sTelegramClient[F](token, client)
}
