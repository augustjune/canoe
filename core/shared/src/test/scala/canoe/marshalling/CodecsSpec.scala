package canoe.marshalling

import canoe.marshalling.codecs._
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder, Json}
import org.scalatest.freespec.AnyFreeSpec

class CodecsSpec extends AnyFreeSpec {
  case class Inner(longName: String)
  case class Outer(longInt: Int, inner: Inner)

  implicit val innerDecoder: Decoder[Inner] = deriveDecoder
  implicit val innerEncoder: Encoder[Inner] = deriveEncoder

  val decoder: Decoder[Outer] = deriveDecoder
  val encoder: Encoder[Outer] = deriveEncoder

  val instance: Outer = Outer(12, Inner("name"))

  "encoder works as expected" in {
    assert(allJsonKeys(encoder(instance)) == List("longInt", "inner", "longName"))
  }

  "snake case encoder encodes keys in snake_case manner" in {
    val encodedKeys = allJsonKeys(encoder.snakeCase(instance))

    assert(encodedKeys.map(_.snakeCase) == encodedKeys)
  }

  "encoded snake_case is decoded in camelCase" in {
    val encodedDecoded = decoder.camelCase.decodeJson(encoder.snakeCase(instance))

    assert(encodedDecoded.contains(instance))
  }

  "either decoder decodes left" in {
    val decoder: Decoder[Either[Int, String]] = eitherDecoder(Decoder.decodeInt, Decoder.decodeString)
    val res = decoder.decodeJson(Json.fromInt(12))

    assert(res.exists(_.isLeft))
  }

  "either decoder decodes right" in {
    val decoder: Decoder[Either[Int, String]] = eitherDecoder(Decoder.decodeInt, Decoder.decodeString)
    val res = decoder.decodeJson(Json.fromString("dasd"))

    assert(res.exists(_.isRight))
  }

  def allJsonKeys(json: Json): List[String] =
    json.asObject.toList.flatMap(_.toList).flatMap {
      case (k, json) => k :: allJsonKeys(json)
    }
}
