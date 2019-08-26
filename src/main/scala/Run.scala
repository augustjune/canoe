import java.nio.file.{Files, Paths}

import canoe.api.syntax._
import canoe.api.{Bot, pipes, _}
import canoe.clients.SttpTelegramClient
import canoe.methods.messages.SendPhoto
import canoe.models.messages.{AudioMessage, TextMessage}
import canoe.models.{Chat, ChatId, InputFile, InputMediaPhoto}
import canoe.scenarios.Scenario
import cats.Show
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
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
  implicit val client = new SttpTelegramClient[IO](token)
  val bot = new Bot(client)

  val respondAudio: Scenario[IO, Unit] =
    for {
      m <- Scenario.start { case m: AudioMessage => m }
      _ <- Scenario.eval(m.chat.send(m.audio))
    } yield ()

  def count(chat: Chat, d: FiniteDuration, i: Int): IO[Unit] =
    if (i > 10) IO.unit
    else for {
      _ <- chat.send(s"$i...")
      _ <- IO.sleep(d)
      _ <- count(chat, d, i + 1)
    } yield ()

  val counter: Scenario[IO, Unit] =
    for {
      m <- Scenario.start { case m: TextMessage if m.text.startsWith("/count") => m }
      start = Try(m.text.split(" ")(1).toInt).getOrElse(0)
      _ <- Scenario.eval(count(m.chat, 1.second, start).start)
    } yield ()

  val repeat: Scenario[IO, Unit] =
    for {
      m <- Scenario.next { case m: TextMessage => m }
      _ <- if (m.text.contains("stop")) Scenario.eval(m.chat.send("Ok, that's all"))
      else Scenario.eval(m.chat.send(m.text)).flatMap(_ => repeat)
    } yield ()

  val mock: Scenario[IO, Unit] =
    for {
      start <- Scenario.start { case m: TextMessage if m.text.contains("start") => m }
      _ <- Scenario.eval(start.chat.send("Starting mocking"))
      _ <- repeat
    } yield ()

  val repsondMediaGroup: Scenario[IO, Unit] =
  for {
    chat <- Scenario.start { case m: TextMessage if m.text.contains("group") => m.chat }
    messages <- Scenario.eval(chat.sendAlbum(List.fill(10)(InputMediaPhoto(InputFile.fromUrl("https://ichef.bbci.co.uk/news/660/cpsprodpb/1486F/production/_105597048_snakes8.jpg")))))
    _ <- Scenario.eval(IO(println(s"Group messages [${{messages.length}}]: ${messages.map(_.messageId)}")))
  } yield ()

  val updates = bot.follow(repsondMediaGroup)

  val polls = pipes.pollUpdates[IO].andThen(_.evalMap(p => IO.unit))

  import io.circe.syntax._

  implicit def showEnc[T: Encoder]: Show[T] =
    _.asJson.pretty(Printer.spaces2.copy(dropNullValues = true))

  val sttpTelegramClient = new SttpTelegramClient[IO](token)

  import canoe.methods.updates.GetUpdates
  import cats.instances.list._

  val chatId = 419510981

  def run(args: List[String]): IO[ExitCode] =
    for {
      updates <- sttpTelegramClient.execute(GetUpdates())
      _ <- updates.traverse(u => IO(println(u)))
      _ <- sttpTelegramClient.execute(SendPhoto(ChatId(chatId), InputFile.fromBytes("rhino", Files.readAllBytes(Paths.get("./rhino.jpg")))))
      _ <- IO(sttpBackend.close())
    } yield ExitCode.Success
}
