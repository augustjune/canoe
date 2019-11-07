package canoe.api

import canoe.methods.Method

/**
  * Client which is able to execute Telegram Bot API methods in effect `F`.
  */
trait TelegramClient[F[_]] {

  /**
    * Transforms request into result using implicit method definition as a contract.
    */
  def execute[Req, Res](request: Req)(implicit M: Method[Req, Res]): F[Res]
}
