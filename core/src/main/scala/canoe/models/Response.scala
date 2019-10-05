package canoe.models

import canoe.marshalling.codecs._
import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

/**
  * Telegram Bot API Response object
  *
  * The response contains a JSON object. If 'ok' equals true, the request was successful and the result of the query can be found in the 'result' field.
  * In case of an unsuccessful request, 'ok' equals false and the error is explained in the 'description'.
  * An Integer 'error_code' field is also returned, but its contents are subject to change in the future.
  *
  * @param ok          Boolean Signals if the request was successful
  * @param result      Optional R Contains the response in a type-safely way
  * @param description Optional String A human-readable description of the result
  * @param errorCode   Optional Integer Error code
  * @tparam R Expected result type
  */
final case class Response[R](ok: Boolean,
                             result: Option[R],
                             description: Option[String],
                             errorCode: Option[Int],
                             parameters: Option[ResponseParameters])

object Response {
  import io.circe.generic.auto._

  implicit def decoder[A: Decoder]: Decoder[Response[A]] =
    deriveDecoder[Response[A]].camelCase
}
