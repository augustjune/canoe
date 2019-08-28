package canoe.marshalling

import cats.free.Trampoline
import cats.instances.function._
import cats.instances.list._
import cats.syntax.traverse._
import io.circe.parser.parse
import io.circe.syntax._
import io.circe.{Json, JsonObject, _}

object marshalling extends CirceEncoders with CirceDecoders with CaseConversions {

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

  def camelKeys(json: Json): Json = transformKeys(json, camelize).run

  def snakeKeys(json: Json): Json = transformKeys(json, snakenize).run

  val printer: Printer = Printer.noSpaces.copy(dropNullValues = true)

  def toJson[T: Encoder](t: T): String = printer.pretty(snakeKeys(t.asJson))

  def fromJson[T: Decoder](s: String): T = {
    parse(s).fold(throw _, json => camelKeys(json).as[T].fold(throw _, identity))
  }
}
