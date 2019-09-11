package canoe.api

import canoe.models.messages.TextMessage
import canoe.models.{MessageReceived, PrivateChat, Update}
import canoe.syntax._
import cats.effect.concurrent.Ref
import cats.effect.{ContextShift, IO, Timer}
import fs2.Stream
import org.scalatest.FunSuite

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class BotSpec extends FunSuite {

  implicit class IOStreamOps[A](stream: Stream[IO, A]) {
    def toList(): List[A] = stream.compile.toList.unsafeRunSync()

    def value(): A = toList().head

    def size(): Int = toList().size

    def run(): Unit = stream.compile.drain.unsafeRunSync()
  }

  implicit val globalContext: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  implicit val globalTimer: Timer[IO] = IO.timer(ExecutionContext.global)
  type Message = String
  type ChatId = Int

  def updateSource(messages: List[(Message, ChatId)]): UpdateSource[IO] =
    new UpdateSource[IO] {
      def updates: Stream[IO, Update] =
        Stream
          .emits(messages.zipWithIndex.map {
            case ((m, id), i) => MessageReceived(i, TextMessage(-1, PrivateChat(id, None, None, None), -1, m))
          })
          .covary[IO]
          .metered(0.1.second)
          .evalTap(s => IO(println(s"Consumed: $s")))
    }

  test("updates returns updates from the source") {
    val messages: List[(Message, ChatId)] = List(
      "1.start" -> 1,
      "2.hello" -> 2
    )

    val bot = new Bot[IO](updateSource(messages))

    val texts = bot.updates.toList().collect {
      case MessageReceived(_, m: TextMessage) => m.text
    }

    assert(texts == messages.map(_._1))
  }

  test("executes a scenario") {

    val messages: List[(Message, ChatId)] = List(
      "message1" -> 1,
      "message2" -> 1,
      "message3" -> 1,
      "message5" -> 1,
      "messag6e" -> 1
    )

    def scenario(counter: Ref[IO, Int]): Scenario[IO, Unit] =
      for {
        _ <- Scenario.start(any)
        _ <- Scenario.eval(counter.update(_ + 1))
      } yield ()

    val bot = new Bot[IO](updateSource(messages))

    val counterValue = Stream
      .eval(Ref[IO].of(0))
      .flatMap { counter =>
        bot.follow(scenario(counter)).drain ++ Stream.eval(counter.get)
      }
      .value()

    assert(counterValue == messages.size)
  }

  test("executes a scenario for a single chat") {
    val chatId = 1
    val messages: List[(Message, ChatId)] = List(
      "start" -> chatId,
      "end" -> chatId
    )

    def scenario(counter: Ref[IO, Int]): Scenario[IO, Unit] =
      for {
        _ <- Scenario.start(text.when(_ == "start"))
        _ <- Scenario.next(text.when(_ == "end"))
        _ <- Scenario.eval(counter.update(_ + 1))
      } yield ()

    val bot = new Bot[IO](updateSource(messages))

    val counterValue = Stream
      .eval(Ref[IO].of(0))
      .flatMap { counter =>
        bot.follow(scenario(counter)).drain ++ Stream.eval(counter.get)
      }
      .value()

    assert(counterValue == 1)
  }

  test("doesn't interrupt the scenario because of messages from other chat") {
    val chatId = 1
    val messages: List[(Message, ChatId)] = List(
      "start" -> chatId,
      "interupt" -> (chatId + 1),
      "end" -> chatId
    )

    def scenario(counter: Ref[IO, Int]): Scenario[IO, Unit] =
      for {
        _ <- Scenario.start(text.when(_ == "start"))
        _ <- Scenario.next(text.when(_ == "end"))
        _ <- Scenario.eval(counter.update(_ + 1))
      } yield ()

    val bot = new Bot[IO](updateSource(messages))

    val counterValue = Stream
      .eval(Ref[IO].of(0))
      .flatMap { counter =>
        bot.follow(scenario(counter)).drain ++ Stream.eval(counter.get)
      }
      .value()

    assert(counterValue == 1)
  }

  test("matches scenario from different chats") {
    val chat1Id = 1
    val chat2Id = 2
    val messages: List[(Message, ChatId)] = List(
      "start" -> chat1Id,
      "start" -> chat2Id,
      "end" -> chat1Id,
      "end" -> chat2Id
    )

    def scenario(counter: Ref[IO, Int]): Scenario[IO, Unit] =
      for {
        _ <- Scenario.start(text.when(_ == "start"))
        _ <- Scenario.next(text.when(_ == "end"))
        _ <- Scenario.eval(counter.update(_ + 1))
      } yield ()

    val bot = new Bot[IO](updateSource(messages))

    val counterValue = Stream
      .eval(Ref[IO].of(0))
      .flatMap { counter =>
        bot.follow(scenario(counter)).drain ++ Stream.eval(counter.get)
      }
      .value()

    assert(counterValue == 2)
  }
}
