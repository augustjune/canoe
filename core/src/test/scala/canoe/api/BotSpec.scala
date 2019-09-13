package canoe.api

import canoe.TestIO._
import canoe.models.messages.TextMessage
import canoe.models.{MessageReceived, PrivateChat, Update}
import canoe.syntax._
import cats.effect.IO
import cats.effect.concurrent.Ref
import fs2.Stream
import org.scalatest.FunSuite

import scala.concurrent.duration._

class BotSpec extends FunSuite {
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
          .metered(0.2.second)
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
      "message6" -> 1
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
      "interrupt" -> (chatId + 1),
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
