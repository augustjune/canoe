import cats.Show
import cats.effect.concurrent.Ref
import cats.effect.{Bracket, ContextShift, ExitCode, IO, IOApp}
import cats.implicits._
import com.canoe.telegram.api._
import com.canoe.telegram.api.syntax._
import com.canoe.telegram.clients.SttpClient
import com.canoe.telegram.marshalling.CirceEncoders._
import com.canoe.telegram.models.Chat
import com.canoe.telegram.models.messages.{AudioMessage, TelegramMessage, TextMessage}
import com.canoe.telegram.models.outgoing.BotMessage
import com.canoe.telegram.scenarios.{Interaction, Scenario}
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

  val respondAudio: Interaction[IO, Unit] =
    for {
      m <- Interaction.receive { case m: AudioMessage => m }
      _ <- Interaction.eval(m.chat.send(BotMessage(m.audio)))
    } yield ()

  def count(chat: Chat, d: FiniteDuration, i: Int): IO[Unit] =
    if (i > 10) IO.unit
    else for {
      _ <- chat.send(BotMessage(s"$i..."))
      _ <- IO.sleep(d)
      _ <- count(chat, d, i + 1)
    } yield ()

  val counter: Interaction[IO, Unit] =
    for {
      m <- Interaction.receive { case m: TextMessage if m.text.startsWith("/count") => m }
      start = Try(m.text.split(" ")(1).toInt).getOrElse(0)
      _ <- Interaction.eval(count(m.chat, 1.second, start).start)
    } yield ()

  val repeat: Interaction[IO, Unit] =
    for {
      m <- Interaction.expect { case m: TextMessage => m }
      _ <- if (m.text.contains("stop")) Scenario.eval(m.chat.send(BotMessage("Ok, that's all")))
      else Interaction.eval(m.chat.send(BotMessage(m.text))).flatMap(_ => repeat)
    } yield ()

  val mock: Interaction[IO, Unit] =
    for {
      start <- Interaction.receive { case m: TextMessage if m.text.contains("start") => m }
      _ <- Interaction.eval(start.chat.send(BotMessage("Starting mocking")))
      _ <- repeat
    } yield ()

  val updates = bot.follow(counter)

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
