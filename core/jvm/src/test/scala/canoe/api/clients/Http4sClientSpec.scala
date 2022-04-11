package canoe.api.clients

import canoe.IOSpec
import canoe.api._
import canoe.methods.Method
import canoe.models.InputFile
import cats.effect.IO
import io.circe.{Decoder, Encoder, Json}
import org.http4s.HttpApp
import org.http4s.client.Client
import org.http4s.dsl.io._
import org.scalatest.freespec.AsyncFreeSpec
import org.typelevel.ci.CIStringSyntax
import org.typelevel.log4cats.Logger

class Http4sClientSpec extends AsyncFreeSpec with IOSpec {
  private case class TestMethod(name: String = "test",
                                encoder: Encoder[String] = Encoder.encodeString,
                                decoder: Decoder[String] = Decoder.decodeString,
                                files: List[InputFile] = Nil
  ) extends Method[String, String] {
    def attachments(request: String): List[(String, InputFile)] = files.map("" -> _)
  }

  private implicit val testMethod: TestMethod = TestMethod()

  private def response(s: String) = s"""{"ok" : true, "result" : "$s"}"""

  private implicit val logger: Logger[IO] = new Logger[IO] {
    def error(message: => String): IO[Unit] = IO.consoleForIO.println(message)
    def warn(message: => String): IO[Unit] = IO.consoleForIO.println(message)
    def info(message: => String): IO[Unit] = IO.consoleForIO.println(message)
    def debug(message: => String): IO[Unit] = IO.consoleForIO.println(message)
    def trace(message: => String): IO[Unit] = IO.consoleForIO.println(message)
    def error(t: Throwable)(message: => String): IO[Unit] = IO.consoleForIO.errorln(message)
    def warn(t: Throwable)(message: => String): IO[Unit] = IO.consoleForIO.errorln(message)
    def info(t: Throwable)(message: => String): IO[Unit] = IO.consoleForIO.errorln(message)
    def debug(t: Throwable)(message: => String): IO[Unit] = IO.consoleForIO.errorln(message)
    def trace(t: Throwable)(message: => String): IO[Unit] = IO.consoleForIO.errorln(message)
  }

  "Client" - {
    "sends" - {
      "to correct Telegram endpoint" in {
        val client: Client[IO] = Client.fromHttpApp(HttpApp(r => Ok(response(r.uri.toString))))
        val tgClient = new Http4sTelegramClient("token", client)

        for {
          result    <- tgClient.execute("any")
          assertion <- IO(assert(result == s"https://api.telegram.org/bottoken/${testMethod.name}"))
        } yield assertion
      }

      val tgClient = new Http4sTelegramClient(
        "",
        Client.fromHttpApp(HttpApp[IO] { r =>
          Ok(response(r.headers.get(ci"Content-Type").map(_.head.value.replace("\"", "''")).getOrElse("")))
        })
      )

      "json POST request if attachments contain file upload" in {
        for {
          result    <- tgClient.execute("any")
          assertion <- IO(assert(result == "application/json"))
        } yield assertion
      }

      "multipart POST request if attachments contain file upload" in {
        for {
          result    <- tgClient.execute("any")(testMethod.copy(files = List(InputFile.Upload("", Array.emptyByteArray))))
          assertion <- IO(assert(result.startsWith("multipart/form-data")))
        } yield assertion
      }
    }

    "encodes/decodes" - {
      "request entity with method encoder" in {
        val tgClient = new Http4sTelegramClient(
          "",
          Client.fromHttpApp(HttpApp[IO](_.bodyText.compile.string.flatMap(s => Ok(response(s.replace("\"", "'"))))))
        )

        for {
          result    <- tgClient.execute("")(testMethod.copy(encoder = Encoder.instance(_ => Json.fromString("encoded"))))
          assertion <- IO(assert(result == "'encoded'"))
        } yield assertion
      }

      "result entity with method decoder" in {
        val tgClient = new Http4sTelegramClient("", Client.fromHttpApp(HttpApp[IO](_ => Ok(response("")))))

        for {
          result    <- tgClient.execute("")(testMethod.copy(decoder = Decoder.const("decoded")))
          assertion <- IO(assert(result == "decoded"))
        } yield assertion
      }
    }

    "handles" - {
      "decode failure as ResponseDecodingError" in {
        val tgClient = new Http4sTelegramClient("", Client.fromHttpApp(HttpApp[IO](_ => Ok("{}"))))

        for {
          result    <- tgClient.execute("any").attempt
          assertion <- IO(assertThrows[ResponseDecodingError](result.left.foreach(throw _)))
        } yield assertion
      }

      "unsuccessful result as FailedMethod" in {
        val response = """{"ok" : false, "result" : "any"}"""
        val tgClient = new Http4sTelegramClient("", Client.fromHttpApp(HttpApp[IO](_ => Ok(response))))

        for {
          result    <- tgClient.execute("any").attempt
          assertion <- IO(assertThrows[FailedMethod[String, String]](result.left.foreach(throw _)))
        } yield assertion
      }
    }
  }
}
