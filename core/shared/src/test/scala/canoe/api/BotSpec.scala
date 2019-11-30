package canoe.api

import canoe.TestIO._
import canoe.models.messages.TextMessage
import canoe.models.{MessageReceived, PrivateChat, Update}
import canoe.syntax._
import cats.effect.IO
import cats.effect.concurrent.Ref
import fs2.Stream
import org.scalatest.funsuite.AnyFunSuite

import scala.concurrent.duration._

class BotSpec extends AnyFunSuite {
  type Message = String
  type ChatId = Int

  def updateSource(messages: List[(Message, ChatId)]): UpdateSource[IO] =
    new UpdateSource[IO] {
      def updates: Stream[IO, Update] =
        Stream
          .emits(messages.zipWithIndex.map {
            case ((m, id), i) => MessageReceived(i, TextMessage(i, PrivateChat(id, None, None, None), -1, m))
          })
          .metered[IO](0.1.second)
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

    val counter = Stream
      .eval(Ref[IO].of(0))
      .flatMap { counter =>
        bot.follow(scenario(counter)).drain ++ Stream.eval(counter.get)
      }

    assert(counter.value() == messages.size)
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

    val counter = Stream
      .eval(Ref[IO].of(0))
      .flatMap { counter =>
        bot.follow(scenario(counter)).drain ++ Stream.eval(counter.get)
      }

    assert(counter.value() == 1)
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

    val counter = Stream
      .eval(Ref[IO].of(0))
      .flatMap { counter =>
        bot.follow(scenario(counter)).drain ++ Stream.eval(counter.get)
      }

    assert(counter.value() == 1)
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

    val counter = Stream
      .eval(Ref[IO].of(0))
      .flatMap { counter =>
        bot.follow(scenario(counter)).drain ++ Stream.eval(counter.get)
      }

    assert(counter.value() == 2)
  }

  test("handles more than one scenario") {
    def scenario1(registed: Ref[IO, Set[Int]]): Scenario[IO, Unit] =
      for {
        _ <- Scenario.start(text.when(_ == "start"))
        _ <- Scenario.next(text.when(_ == "end"))
        _ <- Scenario.eval(registed.update(_ + 1))
      } yield ()

    def scenario2(registed: Ref[IO, Set[Int]]): Scenario[IO, Unit] =
      for {
        _ <- Scenario.start(any)
        _ <- Scenario.eval(registed.update(_ + 2))
      } yield ()

    val messages: List[(Message, ChatId)] = List(
      "start" -> 1,
      "end" -> 1
    )

    val bot = new Bot[IO](updateSource(messages))

    val register = Stream
      .eval(Ref[IO].of(Set.empty[Int]))
      .flatMap { reg =>
        bot.follow(scenario1(reg), scenario2(reg)).drain ++ Stream.eval(reg.get)
      }

    assert(register.value().size == 2)
  }

  test("scenarios don't block each other") {
    def scenario1(registed: Ref[IO, Set[Int]]): Scenario[IO, Unit] =
      for {
        _ <- Scenario.start(text.when(_ == "start"))
        _ <- Scenario.next(text.when(_ == "end"))
        _ <- Scenario.eval(registed.update(_ + 1))
        _ <- Scenario.eval(IO.never)
      } yield ()

    def scenario2(registed: Ref[IO, Set[Int]]): Scenario[IO, Unit] =
      for {
        _ <- Scenario.start(any)
        _ <- Scenario.eval(registed.update(_ + 2))
        _ <- Scenario.eval(IO.never)
      } yield ()

    val messages: List[(Message, ChatId)] = List(
      "start" -> 1,
      "end" -> 1
    )

    val bot = new Bot[IO](updateSource(messages))

    val register = Stream
      .eval(Ref[IO].of(Set.empty[Int]))
      .flatMap { reg =>
        bot.follow(scenario1(reg), scenario2(reg)).drain ++ Stream.eval(reg.get)
      }

    assert(register.value().size == 2)
  }

  test("single scenario evaluation is not blocked between different chats") {
    def scenario1(registed: Ref[IO, Set[Int]]): Scenario[IO, Unit] =
      for {
        m <- Scenario.start(any)
        _ <- Scenario.eval(registed.update(_ + m.messageId))
        _ <- Scenario.eval(IO.never)
      } yield ()

    val messages: List[(Message, ChatId)] = List(
      "first" -> 1,
      "second" -> 2
    )

    val bot = new Bot[IO](updateSource(messages))

    val register = Stream
      .eval(Ref[IO].of(Set.empty[Int]))
      .flatMap { reg =>
        bot.follow(scenario1(reg)).drain ++ Stream.eval(reg.get)
      }

    assert(register.value().size == 2)
  }

  test("single scenario evaluation is not blocked with same chat") {
    def scenario1(registed: Ref[IO, Set[Int]]): Scenario[IO, Unit] =
      for {
        m <- Scenario.start(any)
        _ <- Scenario.eval(registed.update(_ + m.messageId))
        _ <- Scenario.eval(IO.never)
      } yield ()

    val messages: List[(Message, ChatId)] = List(
      "first" -> 1,
      "second" -> 1
    )

    val bot = new Bot[IO](updateSource(messages))

    val register = Stream
      .eval(Ref[IO].of(Set.empty[Int]))
      .flatMap { reg =>
        bot.follow(scenario1(reg)).drain ++ Stream.eval(reg.get)
      }

    assert(register.value().size == 2)
  }
}
