package canoe.clients

import canoe.methods.Method

trait TelegramClient[F[_]] {

  def execute[Req, Res](request: Req)(implicit M: Method[Req, Res]): F[Res]
}
