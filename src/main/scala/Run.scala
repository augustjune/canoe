import cats.Show
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.canoe.telegram.api._
import com.canoe.telegram.clients.SttpClient
import com.canoe.telegram.models.outgoing._
import com.softwaremill.sttp.asynchttpclient.cats.AsyncHttpClientCatsBackend
import com.typesafe.config.ConfigFactory
import fs2.Stream
import io.circe.{Encoder, Printer}

object Run extends IOApp {
  val token = ConfigFactory.parseResources("credentials/telegram.conf").getString("token")

  implicit val sttpBackend = AsyncHttpClientCatsBackend[cats.effect.IO]()
  implicit val client = new SttpClient[IO](token)

  import io.circe.syntax._

  implicit def showEnc[T: Encoder]: Show[T] =
    _.asJson.pretty(Printer.spaces2.copy(dropNullValues = true))



  def run(args: List[String]): IO[ExitCode] =
    for {
      bot <- Bot.polling[IO]
      _ <- bot.start.concurrently(
        bot.messages.evalMap(_.reply(BotMessage(TextContent("Hello biatch!"))))
      ).compile.drain
    } yield ExitCode.Success
}
