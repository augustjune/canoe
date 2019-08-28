package canoe.marshalling

import cats.free.Trampoline
import cats.instances.function._
import cats.instances.list._
import cats.syntax.traverse._
import io.circe.parser.parse
import io.circe._

object codecs extends CaseConversions {

  implicit class EncoderOps[A](private val encoder: Encoder[A]) extends AnyVal {
    def snakeCase: Encoder[A] =
      encoder
        .mapJson(j => parse(printer.pretty(snakeKeys(j)))
            .getOrElse(throw new RuntimeException("Exception during encoding with snake_case")))
  }

  implicit class DecoderOps[A](private val decoder: Decoder[A]) extends AnyVal {
    def camelCase: Decoder[A] =
      decoder.prepare(
        _.top
          .map(camelKeys)
          .map(_.hcursor)
          .getOrElse(throw new RuntimeException("Exception during decoding to camelCase"))
      )
  }

  private val printer: Printer = Printer.noSpaces.copy(dropNullValues = true)

  private def camelKeys(json: Json): Json = transformKeys(json, camelize).run

  private def snakeKeys(json: Json): Json = transformKeys(json, snakenize).run

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
