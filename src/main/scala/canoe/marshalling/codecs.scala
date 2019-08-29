package canoe.marshalling

import cats.free.Trampoline
import cats.instances.function._
import cats.instances.list._
import cats.syntax.traverse._
import io.circe._
import io.circe.parser.parse

object codecs {

  def eitherDecoder[A, B](decA: Decoder[A], decB: Decoder[B]): Decoder[Either[A, B]] = {
    val l: Decoder[Either[A, B]] = decA.map(Left.apply)
    val r: Decoder[Either[A, B]] = decB.map(Right.apply)
    l or r
  }

  implicit class EncoderOps[A](private val encoder: Encoder[A]) extends AnyVal {
    def snakeCase: Encoder[A] =
      encoder
        .mapJson(j => parse(printer.pretty(snakeKeys(j)))
            .getOrElse(throw new RuntimeException("Exception during encoding with snake_case")))
  }

  implicit class DecoderOps[A](private val decoder: Decoder[A]) extends AnyVal {
    def camelCase: Decoder[A] =
      decoder.prepare( c =>
        c.focus match {
          case Some(json) => camelKeys(json).hcursor
          case None => c
        }
      )
  }

  private val printer: Printer = Printer.noSpaces.copy(dropNullValues = true)

  private def camelKeys(json: Json): Json = transformKeys(json, _.camelCase).run

  private def snakeKeys(json: Json): Json = transformKeys(json, _.snakeCase).run

  private def transformKeys(json: Json, f: String => String): Trampoline[Json] = {
    def transformObjectKeys(obj: JsonObject, f: String => String): JsonObject =
      JsonObject.fromIterable(
        obj.toList.map {
          case (k, v) => f(k) -> v
        }
      )

    json.arrayOrObject(
      Trampoline.done(json),
      _.toList.traverse(j => Trampoline.defer(transformKeys(j, f))).map(Json.fromValues(_)),
      transformObjectKeys(_, f).traverse(obj => Trampoline.defer(transformKeys(obj, f))).map(Json.fromJsonObject)
    )
  }
}
