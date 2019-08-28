package canoe.clients

import canoe.methods.Method
import cats.effect.{ConcurrentEffect, Resource, Sync}
import org.http4s.client.Client
import org.http4s.client.blaze.BlazeClientBuilder

import scala.concurrent.ExecutionContext

trait TelegramClient[F[_]] {

  def execute[Req, Res](request: Req)(implicit M: Method[Req, Res]): F[Res]
}

object TelegramClient {

  def apply[F[_]: ConcurrentEffect](token: String, ec: ExecutionContext): Resource[F, TelegramClient[F]] =
    BlazeClientBuilder[F](ec).resource.map(new Http4sTelegramClient[F](token, _))

  def fromHttp4sClient[F[_]: Sync](token: String)(client: Client[F]): TelegramClient[F] =
    new Http4sTelegramClient[F](token, client)
}
