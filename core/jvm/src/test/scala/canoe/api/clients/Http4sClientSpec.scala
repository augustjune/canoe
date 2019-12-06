package canoe.api.clients

import canoe.api._
import canoe.methods.Method
import canoe.models.InputFile
import cats.effect.IO
import io.circe.{Decoder, Encoder}
import org.http4s.HttpApp
import org.http4s.client.Client
import org.http4s.dsl.io._
import io.circe.Json
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import org.scalatest.freespec.AnyFreeSpec

class Http4sClientSpec extends AnyFreeSpec {
  private case class TestMethod(name: String = "test",
                                encoder: Encoder[String] = Encoder.encodeString,
                                decoder: Decoder[String] = Decoder.decodeString,
                                files: List[InputFile] = Nil)
      extends Method[String, String] {
    def attachments(request: String): List[(String, InputFile)] = files.map("" -> _)
  }

  private implicit val testMethod = TestMethod()

  private def response(s: String) = s"""{"ok" : true, "result" : "$s"}"""

  private implicit val logger = Slf4jLogger.getLogger[IO]

  "Client" - {
    "sends" - {
      "to correct Telegram endpoint" in {
        val client: Client[IO] = Client.fromHttpApp(HttpApp(r => Ok(response(r.uri.toString))))
        val tgClient = new Http4sTelegramClient("token", client)

        assert(tgClient.execute("any").unsafeRunSync() == s"https://api.telegram.org/bottoken/${testMethod.name}")
      }

      val tgClient = new Http4sTelegramClient("", Client.fromHttpApp(HttpApp[IO] { r =>
        Ok(response(r.headers.get(org.http4s.headers.`Content-Type`).map(_.value.replace("\"", "''")).getOrElse("")))
      }))

      "json POST request if attachments contain file upload" in {
        assert(tgClient.execute("any").unsafeRunSync() == "application/json")
      }

      "multipart POST request if attachments contain file upload" in {
        val resp = tgClient.execute("any")(testMethod.copy(files = List(InputFile.Upload("", Array.emptyByteArray))))
        assert(resp.unsafeRunSync().startsWith("multipart/form-data"))
      }
    }

    "encodes/decodes" - {
      "request entity with method encoder" in {
        val tgClient = new Http4sTelegramClient(
          "",
          Client.fromHttpApp(HttpApp[IO](_.bodyAsText.compile.string.flatMap(s => Ok(response(s.replace("\"", "'"))))))
        )
        val res = tgClient.execute("")(testMethod.copy(encoder = Encoder.instance(_ => Json.fromString("encoded"))))

        assert(res.unsafeRunSync() == "'encoded'")
      }

      "result entity with method decoder" in {
        val tgClient = new Http4sTelegramClient("", Client.fromHttpApp(HttpApp[IO](_ => Ok(response("")))))
        val res = tgClient.execute("")(testMethod.copy(decoder = Decoder.const("decoded")))

        assert(res.unsafeRunSync() == "decoded")
      }
    }

    "handles" - {
      "decode failure as ResponseDecodingError" in {
        val tgClient = new Http4sTelegramClient("", Client.fromHttpApp(HttpApp[IO](_ => Ok("{}"))))

        assertThrows[ResponseDecodingError](tgClient.execute("any").unsafeRunSync())
      }

      "unsuccessful result as FailedMethod" in {
        val response = """{"ok" : false, "result" : "any"}"""
        val tgClient = new Http4sTelegramClient("", Client.fromHttpApp(HttpApp[IO](_ => Ok(response))))

        assertThrows[FailedMethod[String, String]](tgClient.execute("any").unsafeRunSync())
      }
    }
  }
}
