package canoe.api

import canoe.IOSpec
import canoe.models.messages.TextMessage
import canoe.models.{MessageReceived, PrivateChat, Update}
import canoe.syntax._
import cats.effect.{IO, Ref}
import fs2.Stream
import org.scalatest.freespec.AsyncFreeSpec

import scala.concurrent.duration._

class BotSpec extends AsyncFreeSpec with IOSpec {
  type Message = String
  type ChatId = Int

  def updates(messages: List[(Message, ChatId)]): Stream[IO, Update] =
    Stream
      .emits(messages.zipWithIndex.map { case ((m, id), i) =>
        MessageReceived(i, TextMessage(i, PrivateChat(id, None, None, None), -1, m))
      })
      .metered[IO](0.2.second)

  "Bot" - {
    "updates" - {
      "are returned from the update source" in {
        val messages: List[(Message, ChatId)] = List(
          "1.start" -> 1,
          "2.hello" -> 2
        )

        val bot = new Bot[IO](updates(messages))

        val texts = bot.updates.toList().collect { case MessageReceived(_, m: TextMessage) =>
          m.text
        }

        assert(texts == messages.map(_._1))
      }
    }

    "follow" - {
      "matches" - {
        "scenario for each incoming message" in {
          val messages: List[(Message, ChatId)] = List(
            "message" -> 1,
            "message" -> 1,
            "message" -> 1
          )

          def scenario(counter: Ref[IO, Int]): Scenario[IO, Unit] =
            for {
              _ <- Scenario.expect(any)
              _ <- Scenario.eval(counter.update(_ + 1))
            } yield ()

          val bot = new Bot[IO](updates(messages))

          val counter = Stream
            .eval(Ref[IO].of(0))
            .flatMap(counter => bot.follow(scenario(counter)).drain ++ Stream.eval(counter.get))

          assert(counter.value() == messages.size)
        }

        "scenario for the messages from different chats" in {
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
              _ <- Scenario.expect(text.when(_ == "start"))
              _ <- Scenario.expect(text.when(_ == "end"))
              _ <- Scenario.eval(counter.update(_ + 1))
            } yield ()

          val bot = new Bot[IO](updates(messages))

          val counter = Stream
            .eval(Ref[IO].of(0))
            .flatMap(counter => bot.follow(scenario(counter)).drain ++ Stream.eval(counter.get))

          assert(counter.value() == 2)
        }

        "more than one scenario" in {
          def scenario1(registed: Ref[IO, Set[Int]]): Scenario[IO, Unit] =
            for {
              _ <- Scenario.expect(text.when(_ == "start"))
              _ <- Scenario.expect(text.when(_ == "end"))
              _ <- Scenario.eval(registed.update(_ + 1))
            } yield ()

          def scenario2(registed: Ref[IO, Set[Int]]): Scenario[IO, Unit] =
            for {
              _ <- Scenario.expect(any)
              _ <- Scenario.eval(registed.update(_ + 2))
            } yield ()

          val messages: List[(Message, ChatId)] = List(
            "start" -> 1,
            "end" -> 1
          )

          val bot = new Bot[IO](updates(messages))

          val register = Stream
            .eval(Ref[IO].of(Set.empty[Int]))
            .flatMap(reg => bot.follow(scenario1(reg), scenario2(reg)).drain ++ Stream.eval(reg.get))

          assert(register.value().size == 2)
        }
      }

      "scenario execution is not blocked" - {
        "by other scenario" in {
          def scenario1(registed: Ref[IO, Set[Int]]): Scenario[IO, Unit] =
            for {
              _ <- Scenario.expect(text.when(_ == "start"))
              _ <- Scenario.expect(text.when(_ == "end"))
              _ <- Scenario.eval(registed.update(_ + 1))
              _ <- Scenario.eval(IO.never)
            } yield ()

          def scenario2(registed: Ref[IO, Set[Int]]): Scenario[IO, Unit] =
            for {
              _ <- Scenario.expect(any)
              _ <- Scenario.eval(registed.update(_ + 2))
              _ <- Scenario.eval(IO.never)
            } yield ()

          val messages: List[(Message, ChatId)] = List(
            "start" -> 1,
            "end" -> 1
          )

          val bot = new Bot[IO](updates(messages))

          val register = Stream
            .eval(Ref[IO].of(Set.empty[Int]))
            .flatMap(reg => bot.follow(scenario1(reg), scenario2(reg)).drain ++ Stream.eval(reg.get))

          assert(register.value().size == 2)
        }

        "by the same scenario in other chat" in {
          def scenario1(registed: Ref[IO, Set[Int]]): Scenario[IO, Unit] =
            for {
              m <- Scenario.expect(any)
              _ <- Scenario.eval(registed.update(_ + m.messageId))
              _ <- Scenario.eval(IO.never)
            } yield ()

          val messages: List[(Message, ChatId)] = List(
            "first" -> 1,
            "second" -> 2
          )

          val bot = new Bot[IO](updates(messages))

          val register = Stream
            .eval(Ref[IO].of(Set.empty[Int]))
            .flatMap(reg => bot.follow(scenario1(reg)).drain ++ Stream.eval(reg.get))

          assert(register.value().size == 2)
        }

        "by the same scenario in the same chat" in {
          def scenario1(registed: Ref[IO, Set[Int]]): Scenario[IO, Unit] =
            for {
              m <- Scenario.expect(any)
              _ <- Scenario.eval(registed.update(_ + m.messageId))
              _ <- Scenario.eval(IO.never)
            } yield ()

          val messages: List[(Message, ChatId)] = List(
            "first" -> 1,
            "second" -> 1
          )

          val bot = new Bot[IO](updates(messages))

          val register = Stream
            .eval(Ref[IO].of(Set.empty[Int]))
            .flatMap(reg => bot.follow(scenario1(reg)).drain ++ Stream.eval(reg.get))

          assert(register.value().size == 2)
        }
      }
    }
  }
}
