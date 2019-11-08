package canoe.marshalling

import io.circe._

object codecs {

  def eitherDecoder[A, B](decA: Decoder[A], decB: Decoder[B]): Decoder[Either[A, B]] = {
    val l: Decoder[Either[A, B]] = decA.map(Left(_))
    val r: Decoder[Either[A, B]] = decB.map(Right(_))
    l or r
  }

  implicit class EncoderOps[A](private val encoder: Encoder[A]) extends AnyVal {
    def snakeCase: Encoder[A] =
      encoder.mapJson(
        j =>
          parser
            .parse(printer.print(snakeKeys(j)))
            .getOrElse(throw new RuntimeException("Exception during encoding with snake_case"))
      )
  }

  implicit class DecoderOps[A](private val decoder: Decoder[A]) extends AnyVal {
    def camelCase: Decoder[A] =
      decoder.prepare(c => c.focus.map(camelKeys(_).hcursor).getOrElse(c))
  }

  private val printer: Printer = Printer.noSpaces.copy(dropNullValues = true)

  private val camelKeys: Json => Json = transformKeys(_.camelCase)

  private val snakeKeys: Json => Json = transformKeys(_.snakeCase)

  // Does recursive call, but doesn't require additional stack safety measures
  // if it is used for regular size JSONs (Telegram API)
  private def transformKeys(f: String => String)(json: Json): Json =
    json.arrayOrObject(
      json,
      jArray => Json.fromValues(jArray.map(transformKeys(f))),
      jObject => Json.fromFields(jObject.toList.map { case (k, v) => f(k) -> transformKeys(f)(v) })
    )
}
