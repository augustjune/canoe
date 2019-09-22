package canoe.marshalling

import canoe.marshalling.codecs._
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder, Json}
import org.scalatest.funsuite.AnyFunSuite

class CodecsSpec extends AnyFunSuite {

  case class Inner(longName: String)
  case class Outer(longInt: Int, inner: Inner)

  implicit val innerDecoder: Decoder[Inner] = deriveDecoder
  implicit val innerEncoder: Encoder[Inner] = deriveEncoder

  val decoder: Decoder[Outer] = deriveDecoder
  val encoder: Encoder[Outer] = deriveEncoder

  val instance: Outer = Outer(12, Inner("name"))

  test("encoder works as expected") {
    assert(allJsonKeys(encoder(instance)) == List("longInt", "inner", "longName"))
  }

  test("snake case encoder encodes keys in snake_case manner") {
    val encodedKeys = allJsonKeys(encoder.snakeCase(instance))

    assert(encodedKeys.map(_.snakeCase) == encodedKeys)
  }

  test("encoded snake_case is decoded in camelCase") {
    val encodedDecoded = decoder.camelCase.decodeJson(encoder.snakeCase(instance))

    assert(encodedDecoded.contains(instance))
  }

  test("either decoder decodes left") {
    val decoder: Decoder[Either[Int, String]] = eitherDecoder(Decoder.decodeInt, Decoder.decodeString)
    val res = decoder.decodeJson(Json.fromInt(12))

    assert(res.exists(_.isLeft))
  }

  test("either decoder decodes right") {
    val decoder: Decoder[Either[Int, String]] = eitherDecoder(Decoder.decodeInt, Decoder.decodeString)
    val res = decoder.decodeJson(Json.fromString("dasd"))

    assert(res.exists(_.isRight))
  }

  def allJsonKeys(json: Json): List[String] =
    json.asObject.toList.flatMap(_.toList).flatMap {
      case (k, json) => k :: allJsonKeys(json)
    }
}
