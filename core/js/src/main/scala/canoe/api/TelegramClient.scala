package canoe.api

import canoe.api.clients.AjaxClient
import canoe.methods.Method
import cats.effect.Async

/** Client which is able to execute Telegram Bot API methods in effect `F`.
  */
trait TelegramClient[F[_]] {

  /** Transforms request into result using implicit method definition as a contract.
    */
  def execute[Req, Res](request: Req)(implicit M: Method[Req, Res]): F[Res]
}

object TelegramClient {

  /** Creates an authorized Telegram Bot API client.
    *
    * @param token Bot token
    */
  def apply[F[_]: Async](token: String): TelegramClient[F] =
    new AjaxClient[F](token)
}
