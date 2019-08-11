import cats.Show
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.canoe.telegram.api._
import com.canoe.telegram.api.syntax._
import com.canoe.telegram.clients.SttpClient
import com.canoe.telegram.models.InputFile
import com.canoe.telegram.models.messages.{AudioMessage, TextMessage}
import com.canoe.telegram.models.outgoing.{BotMessage, PhotoContent}
import com.canoe.telegram.scenarios.{Expect, Receive, Scenario}
import com.softwaremill.sttp.asynchttpclient.cats.AsyncHttpClientCatsBackend
import com.typesafe.config.ConfigFactory
import io.circe.{Encoder, Printer}
import com.canoe.telegram.marshalling.CirceEncoders._
import fs2.Pipe

object Run extends IOApp {
  val token: String =
    ConfigFactory.parseResources("credentials/telegram.conf")
      .getString("token")

  implicit val sttpBackend = AsyncHttpClientCatsBackend[cats.effect.IO]()
  implicit val client = new SttpClient[IO](token)
  val bot = new Bot(client)

  val respondAudio: Scenario[IO, Unit] =
    for {
      m <- Receive.collect[IO] { case m: AudioMessage => m }
      _ <- Scenario.eval(m.chat.send(BotMessage(m.audio)))
    } yield ()

  val greetings: Scenario[IO, Unit] =
    for {
      m1 <- Scenario.receive[IO] { case m: TextMessage => m.text.contains("Hi") }
      _ <- Scenario.eval(m1.chat.send(BotMessage("Wassup?")))
      m2 <- (Scenario.expect[IO] { case m: TextMessage => m.text.contains("fine") } or
        Scenario.expect[IO] { case m: TextMessage => m.text.contains("bad") })
        .tolerate(_.reply(BotMessage("Your answer must contain either 'fine' or 'bad'")))

      _ <- m2 match {
        case Left(fine) => Scenario.eval(fine.chat.send(BotMessage("Oh, I'm so happy for you")))
        case Right(bad) => Scenario.eval(bad.chat.send(BotMessage("Oh, I'm sorry. Is there something I can do for you?")))
      }
    } yield ()

  val repeat: Scenario[IO, Unit] =
    for {
      m <- Expect.collect { case m: TextMessage => m }
      _ <-
        if (m.text.contains("stop")) Scenario.eval(m.chat.send(BotMessage("Ok, that's all")))
        else Scenario.eval(m.chat.send(BotMessage(m.text))) >> repeat
    } yield ()

  val mock: Scenario[IO, Unit] =
    for {
      start <- Scenario.receive[IO] { case m: TextMessage => m.text.contains("start") }
      _ <- Scenario.eval(start.chat.send(BotMessage("Starting mocking")))
      _ <- repeat
    } yield ()

  val updates = bot.follow(List(respondAudio, sendFiles))

  val polls = pipes.pollUpdates[IO].andThen(_.evalMap(p => IO.unit))

  import io.circe.syntax._

  implicit def showEnc[T: Encoder]: Show[T] =
    _.asJson.pretty(Printer.spaces2.copy(dropNullValues = true))

  def run(args: List[String]): IO[ExitCode] =
    updates
      .showLinesStdOut
      .compile.drain
      .as(ExitCode.Success)
}
