import cats.Show
import cats.effect.concurrent.Ref
import cats.effect.{Bracket, ContextShift, ExitCode, IO, IOApp}
import cats.implicits._
import com.canoe.telegram.api._
import com.canoe.telegram.api.syntax._
import com.canoe.telegram.clients.SttpClient
import com.canoe.telegram.marshalling.CirceEncoders._
import com.canoe.telegram.models.Chat
import com.canoe.telegram.models.messages.{AudioMessage, TextMessage}
import com.canoe.telegram.models.outgoing.BotMessage
import com.canoe.telegram.scenarios.{Expect, Receive, Scenario}
import com.softwaremill.sttp.asynchttpclient.cats.AsyncHttpClientCatsBackend
import com.typesafe.config.ConfigFactory
import io.circe.{Encoder, Printer}

import scala.concurrent.duration._
import scala.util.Try

object Run extends IOApp {
  val token: String =
    ConfigFactory
      .parseResources("credentials/telegram.conf")
      .getString("token")

  implicit val sttpBackend = AsyncHttpClientCatsBackend[cats.effect.IO]()
  implicit val client = new SttpClient[IO](token)
  val bot = new Bot(client)

  val respondAudio: Scenario[IO, Unit] =
    for {
      m <- Receive.collect[IO] { case m: AudioMessage => m }
      _ <- Scenario.eval(m.chat.send(BotMessage(m.audio)))
    } yield ()

  def count(chat: Chat, d: FiniteDuration, i: Int): IO[Unit] =
    if (i > 10) IO.unit
    else for {
      _ <- chat.send(BotMessage(s"$i..."))
      _ <- IO.sleep(d)
      _ <- count(chat, d, i + 1)
    } yield ()

  val counter: Scenario[IO, Unit] =
    for {
      m <- Receive.collect { case m: TextMessage if m.text.startsWith("/count") => m }
      start = Try(m.text.split(" ")(1).toInt).getOrElse(0)
      _ <- Scenario.eval(count(m.chat, 1.second, start).start)
    } yield ()

  val greetings: Scenario[IO, Unit] =
    for {
      m1 <- Scenario.receive[IO] { case m: TextMessage => m.text.contains("Hi") }
      _ <- Scenario.eval(m1.chat.send(BotMessage("Wassup?")))
      m2 <- Scenario
        .expect[IO] { case m: TextMessage => m.text.contains("fine") }
        .or(Scenario.expect[IO] { case m: TextMessage => m.text.contains("bad") })
        .tolerate(_.reply(BotMessage("Your answer must contain either 'fine' or 'bad'")))

      _ <- m2 match {
        case Left(fine) => Scenario.eval(fine.chat.send(BotMessage("Oh, I'm so happy for you")))
        case Right(bad) =>
          Scenario.eval(
            bad.chat.send(BotMessage("Oh, I'm sorry. Is there something I can do for you?")))
      }
    } yield ()

  val repeat: Scenario[IO, Unit] =
    for {
      m <- Expect.collect { case m: TextMessage => m }
      _ <- if (m.text.contains("stop")) Scenario.eval(m.chat.send(BotMessage("Ok, that's all")))
      else Scenario.eval(m.chat.send(BotMessage(m.text))) >> repeat
    } yield ()

  val mock: Scenario[IO, Unit] =
    for {
      start <- Scenario.receive[IO] { case m: TextMessage => m.text.contains("start") }
      _ <- Scenario.eval(start.chat.send(BotMessage("Starting mocking")))
      _ <- repeat
    } yield ()

  val updates = bot.follow(List(counter))

  val polls = pipes.pollUpdates[IO].andThen(_.evalMap(p => IO.unit))

  import io.circe.syntax._

  implicit def showEnc[T: Encoder]: Show[T] =
    _.asJson.pretty(Printer.spaces2.copy(dropNullValues = true))

  def run(args: List[String]): IO[ExitCode] =
    updates
//      .showLinesStdOut
      .compile.drain
      .as(ExitCode.Success)
}
