import java.nio.file.{Files, Paths}

import cats.Show
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import com.canoe.telegram.api._
import com.canoe.telegram.api.syntax._
import com.canoe.telegram.clients.SttpClient
import com.canoe.telegram.marshalling.CirceEncoders._
import com.canoe.telegram.models.InputFile
import com.canoe.telegram.models.messages.TextMessage
import com.canoe.telegram.models.outgoing.{BotMessage, PhotoContent}
import com.canoe.telegram.scenarios.{Action, Expect, Receive, Scenario}
import com.softwaremill.sttp.asynchttpclient.cats.AsyncHttpClientCatsBackend
import com.typesafe.config.ConfigFactory
import io.circe.{Encoder, Printer}

object Run extends IOApp {
  val token: String = ConfigFactory.parseResources("credentials/telegram.conf").getString("token")

  implicit val sttpBackend = AsyncHttpClientCatsBackend[cats.effect.IO]()
  implicit val client = new SttpClient[IO](token)
  val bot = new Bot(client)

  val sendFiles: Scenario[IO, Unit] =
    for {
      m <- Receive[IO](_ => true)
      _ <- Action(m.chat.send(BotMessage(PhotoContent(InputFile("AgADBAADjK8xG8_WcVL6ZDHt58Fsd_RxqBsABAEAAwIAA20AA4v2AAIWBA"), Some("Kermit the frog here")))))
      _ <- Action(m.chat.send(BotMessage(PhotoContent(InputFile("https://www.google.com/imgres?imgurl=https%3A%2F%2Fa1cf74336522e87f135f-2f21ace9a6cf0052456644b80fa06d4f.ssl.cf2.rackcdn.com%2Fimages%2Fcharacters_opt%2Fp-the-transporter-2-jason-statham.jpg&imgrefurl=https%3A%2F%2Fwww.charactour.com%2Fhub%2Fcharacters%2Fview%2FFrank-Martin.The-Transporter&docid=sFjB3yr6FmtMFM&tbnid=TowqSBOSgcn3nM%3A&vet=10ahUKEwjB0PfamfbjAhVNzRoKHePKDzsQMwiAASgFMAU..i&w=300&h=301&bih=907&biw=1680&q=the%20transporter&ved=0ahUKEwjB0PfamfbjAhVNzRoKHePKDzsQMwiAASgFMAU&iact=mrc&uact=8")))))
      _ <- Action(IO(Files.readAllBytes(Paths.get("./trans.jpg"))).flatMap(bytes => m.chat.send(BotMessage(PhotoContent(InputFile.Upload("Da", bytes))))))
    } yield ()

  val greetings: Scenario[IO, Unit] =
    for {
      m1 <- Receive[IO] {
        case m: TextMessage if m.text.contains("Hi") => true
        case _ => false
      }
      _ <- Action(m1.chat.send(BotMessage("Wassup?")))
      m2 <- (Expect[IO] { case m: TextMessage => println(s"matching $m"); m.text.contains("fine"); case _ => false } or
        Expect[IO] { case m: TextMessage => println(s"matching $m"); m.text.contains("bad"); case _ => false })
        .tolerate(_.reply(BotMessage("Your answer must contain either 'fine' or 'bad'")))

      _ <- m2 match {
        case Left(fine) => Action(fine.chat.send(BotMessage("Oh, I'm so happy for you")))
        case Right(bad) => Action(bad.chat.send(BotMessage("Oh, I'm sorry. Is there something I can do for you?")))
      }
    } yield ()

  val repeat: Scenario[IO, Unit] =
    for {
      m <- Expect(_.isInstanceOf[TextMessage])
      _ <- m match {
        case text: TextMessage =>
          if (text.text.contains("stop")) Action(m.chat.send(BotMessage("Ok, that's all")))
          else Action(m.chat.send(BotMessage(text.text))).flatMap(_ => repeat)
        case _ => Action(IO.unit)
      }
    } yield ()

  val mock: Scenario[IO, Unit] =
    for {
      start <- Receive[IO] { case m: TextMessage => m.text.contains("start"); case _ => false }
      _ <- Action(start.chat.send(BotMessage("Starting mocking")))
      _ <- repeat
    } yield ()


  val updates = bot.follow(List(greetings))

  import io.circe.syntax._

  implicit def showEnc[T: Encoder]: Show[T] =
    _.asJson.pretty(Printer.spaces2.copy(dropNullValues = true))

  def run(args: List[String]): IO[ExitCode] =
    for {
      _ <- updates
        .compile.drain
    } yield ExitCode.Success
}
