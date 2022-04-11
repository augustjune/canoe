package canoe.marshalling

import canoe.marshalling.codecs._
import io.circe.generic.semiauto
import io.circe.generic.auto._
import io.circe.{Codec, Json}
import org.scalatest.freespec.AnyFreeSpec

class CodecsSpec extends AnyFreeSpec {
  case class Inner(longName: String)
  case class Outer(longInt: Int, inner: Inner)

  val codec: Codec[Outer] = semiauto.deriveCodec

  val instance: Outer = Outer(12, Inner("name"))

  "encoder works as expected" in {
    assert(allJsonKeys(codec(instance)) == List("longInt", "inner", "longName"))
  }

  "snake case encoder encodes keys in snake_case manner" in {
    val encodedKeys = allJsonKeys(codec.snakeCase(instance))

    assert(encodedKeys == List("long_int", "inner", "long_name"))
  }

  "encoded snake_case is decoded in camelCase" in {
    val encodedDecoded = codec.camelCase.decodeJson(codec.snakeCase(instance))

    assert(encodedDecoded.contains(instance))
  }

  def allJsonKeys(json: Json): List[String] =
    json.asObject.toList.flatMap(_.toList).flatMap { case (k, json) =>
      k :: allJsonKeys(json)
    }
}
