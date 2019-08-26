package canoe.clients

import canoe.marshalling.{CaseConversions, marshalling}
import canoe.methods._
import canoe.models.{InputFile, Response}
import cats.Functor
import cats.implicits._
import com.softwaremill.sttp.{Method => _, Request => _, Response => _, _}
import io.circe.parser.parse
import io.circe.{Decoder, Encoder}

import scala.concurrent.duration._

class SttpTelegramClient[F[_]](token: String)(implicit backend: SttpBackend[F, Nothing], F: Functor[F]) extends TelegramClient[F] {

  val telegramHost = "api.telegram.org"

  val readTimeout: Duration = 50.seconds
  private implicit def circeBodySerializer[B: Encoder]: BodySerializer[B] =
    b => StringBody(marshalling.toJson[B](b), "utf-8", Some(MediaTypes.Json))

  private def asJson[B: Decoder]: ResponseAs[B, Nothing] =
    asString("utf-8").map(s => marshalling.fromJson[B](s))

  private val apiBaseUrl = s"https://$telegramHost/bot$token/"

  def execute[Req, Res](request: Req)(implicit method: Method[Req, Res]): F[Res] = {
    val url = apiBaseUrl + method.name

    val sttpRequest: RequestT[Id, String, Nothing] = {
      val jsonReq = sttp.post(uri"$url").body(method.encoder(request))

      val parts = method.uploads(request).collect {
        case (key, InputFile.Upload(filename, contents)) =>
          multipart(CaseConversions.snakenize(key), contents)
            .fileName(filename)
      }

      val fields =
        parse(marshalling.toJson(request)(method.encoder)).fold(throw _, _.asObject.map {
          _.toMap.mapValues { json =>
            json.asString.getOrElse(marshalling.printer.pretty(json))
          }
        })

      val params = fields.getOrElse(Map())

      if (parts.isEmpty) jsonReq
      else sttp.post(uri"$url?$params").multipartBody(parts)
    }

    implicit val resDecoder: Decoder[Res] = method.decoder

    val response = sttpRequest
      .readTimeout(readTimeout)
      .parseResponseIf(_ => true) // Always parse response
      .response(asJson[Response[Res]])
      .send[F]()

    response
      .map(_.unsafeBody)
      .map(processApiResponse[Res])
  }

  private def processApiResponse[R](response: Response[R]): R = response match {
    case Response(true, Some(result), _, _, _) => result
    //    case Response(false, _, description, Some(errorCode), parameters) =>
    //      throw new TelegramApiException(description.getOrElse("Unexpected/invalid/empty response"), errorCode, None, parameters)

    case other =>
      throw new RuntimeException(s"Unexpected API response: $other")
  }
}
