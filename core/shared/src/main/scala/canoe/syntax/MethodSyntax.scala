package canoe.syntax

import canoe.api.TelegramClient
import canoe.methods.Method

/**
  * Syntax enhancement for using Telegram methods.
  */
final class MethodSyntax[A](private val a: A) extends AnyVal {

  /**
    * Sends the method to the Telegram service.
    * It allows to execute Telegram methods having `TelegramClient` in implicit scope.
    */
  def call[F[_], R](implicit client: TelegramClient[F], method: Method[A, R]): F[R] =
    client.execute(a)
}
