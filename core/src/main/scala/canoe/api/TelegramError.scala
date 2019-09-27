package canoe.api

import canoe.methods.Method
import canoe.models.Response

sealed trait TelegramError extends Throwable

final case class ResponseDecodingError(json: String) extends TelegramError

final case class FailedMethod[I, A](m: Method[I, A], input: I, response: Response[A]) extends TelegramError
