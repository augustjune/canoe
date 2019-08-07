import cats.Show
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.canoe.telegram.api._
import com.canoe.telegram.clients.SttpClient
import com.canoe.telegram.marshalling.CirceEncoders._
import com.canoe.telegram.models.Update
import com.canoe.telegram.models.messages.TextMessage
import com.canoe.telegram.models.outgoing.{BotMessage, TextContent}
import com.canoe.telegram.scenarios.{Action, ChatScenario, Expect, Receive}
import com.softwaremill.sttp.asynchttpclient.cats.AsyncHttpClientCatsBackend
import com.typesafe.config.ConfigFactory
import io.circe.{Encoder, Printer}

object Run extends IOApp {
  val token: String = ConfigFactory.parseResources("credentials/telegram.conf").getString("token")

  implicit val sttpBackend = AsyncHttpClientCatsBackend[cats.effect.IO]()
  implicit val client = new SttpClient[IO](token)
  val bot = new Bot(client)

  val greetings: ChatScenario[IO, Unit] =
    for {
      m1 <- Receive[IO] {
        case m: TextMessage if m.text.contains("Hi") => true
        case _ => false
      }
      _ <- Action(m1.reply(BotMessage(TextContent("Wassup?"))))
      m2 <- Expect[IO] { case m: TextMessage if m.text.contains("fine") => true; case _ => false } or
        Expect[IO] { case m: TextMessage if m.text.contains("bad") => true; case _ => false }

      _ <- m2 match {
        case Left(fine) => Action(fine.reply(BotMessage(TextContent("Oh, I'm so happy for you"))))
        case Right(bad) => Action(bad.reply(BotMessage(TextContent("Oh, I'm sorry. Is there something I can do for you?"))))
      }
    } yield ()

  val repeat: ChatScenario[IO, Unit] =
    for {
      m <- Expect(_.isInstanceOf[TextMessage])
      _ <- m match {
        case text: TextMessage =>
          if (text.text.contains("stop")) Action(m.reply(BotMessage(TextContent("Ok, that's all"))))
          else Action(m.reply(BotMessage(TextContent(text.text)))).flatMap(_ => repeat)
        case _ => Action(IO.unit)
      }
    } yield ()

  val mock: ChatScenario[IO, Unit] =
    for {
      start <- Receive[IO] { case m: TextMessage => m.text.contains("start"); case _ => false }
      _ <- Action(start.reply(BotMessage(TextContent("Starting mocking"))))
      _ <- repeat
    } yield ()


  val updates = bot.follow(List(greetings, mock))

  import io.circe.syntax._

  implicit def showEnc[T: Encoder]: Show[T] =
    _.asJson.pretty(Printer.spaces2.copy(dropNullValues = true))

  def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- updates.showLinesStdOut.compile.drain
    } yield ExitCode.Success
}
