package canoe.api

import canoe.TestIO._
import canoe.models.PrivateChat
import canoe.models.messages.{TelegramMessage, TextMessage}
import canoe.syntax._
import cats.effect.IO
import fs2.Stream
import org.scalatest.freespec.AnyFreeSpec

class ScenarioSpec extends AnyFreeSpec {
  private def message(s: String): TextMessage =
    TextMessage(-1, PrivateChat(-1, None, None, None), -1, s)

  "Scenario" - {
    "expect" - {
      "consumes at least one message" in {
        val scenario: Scenario[IO, TextMessage] = Scenario.expect(command("fire"))
        val input = Stream.empty

        assert(input.through(scenario.pipe).toList().isEmpty)
      }

      "uses provided predicate to match the result" in {
        val scenario: Scenario[IO, TextMessage] = Scenario.expect(textMessage.endingWith("fire"))
        val input = Stream("").map(message)

        assert(input.through(scenario.pipe).toList().isEmpty)
      }

      "halts the stream if the first element was not matched" in {
        val trigger = "fire"
        val scenario: Scenario[IO, TextMessage] = Scenario.expect(textMessage.matching(trigger))

        val input = Stream("not_matched", trigger).map(message)

        assert(input.through(scenario.pipe).toList().isEmpty)
      }

      "checks only the first message" in {
        val trigger = "fire"
        val scenario: Scenario[IO, TextMessage] = Scenario.expect(textMessage.endingWith(trigger))

        val input = Stream(
          s"1.$trigger",
          s"2.$trigger"
        ).map(message)

        assert(input.through(scenario.pipe).toList() == input.toList.take(1))
      }
    }

    "eval" - {
      "doesn't consume any message" in {
        val scenario: Scenario[IO, Unit] = Scenario.eval(IO.unit)
        val input = Stream.empty

        assert(input.through(scenario.pipe).size() == 1)
      }

      "evaluates value in the effect" in {
        val scenario: Scenario[IO, Int] = Scenario.eval(IO.pure(12))

        assert(Stream.empty.through(scenario.pipe).value() == 12)
      }

      "evaluates effect once" in {
        var counter = 0
        val scenario: Scenario[IO, Unit] = Scenario.eval(IO { counter += 1 })
        Stream.empty.through(scenario.pipe).run()

        assert(counter == 1)
      }

      "propagates error raised in underlying value" in {
        case class Error(s: String) extends Throwable
        val scenario: Scenario[IO, Unit] = Scenario.eval(IO.raiseError(Error("test")))

        assertThrows[Error](Stream.empty.through(scenario.pipe).run())
      }
    }

    "raiseError" - {
      "interrupts the flow of the scenario" in {
        case class Error(s: String) extends Throwable
        val scenario: Scenario[IO, Int] = Scenario.raiseError(Error("test")).map(_ => 12)

        assertThrows[Error](Stream.empty.through(scenario.pipe).value())
      }
    }

    "handleErrorWith" - {
      "transforms into provided substitute in case of error" in {
        case class Error(s: String) extends Throwable
        val scenario: Scenario[IO, Int] =
          Scenario
            .raiseError(Error("test"))
            .flatMap(_ => Scenario.eval(IO.pure(-1)))
            .handleErrorWith(_ => Scenario.eval(IO.pure(12)))

        assert(Stream.empty.through(scenario.pipe).value() == 12)
      }
    }

    "attempt" - {
      "wraps exception in left part of Either" in {
        case class Error(s: String) extends Throwable
        val error = Error("test")

        val scenario: Scenario[IO, Unit] = Scenario.raiseError(error)
        assert(Stream.empty.through(scenario.attempt.pipe).value() == Left(error))
      }

      "wraps result in right part of Either" in {
        val scenario: Scenario[IO, Int] = Scenario.eval(IO.pure(12))
        assert(Stream.empty.through(scenario.attempt.pipe).value() == Right(12))
      }
    }

    "cancellation" - {
      "is done when matching input element is encountered" in {
        val cancelMessage = "cancel"
        val scenario: Scenario[IO, Unit] =
          for {
            _ <- Scenario.expect(any)
            _ <- Scenario.expect(any)
          } yield ()

        val cancellable = scenario.cancelOn(textMessage.matching(cancelMessage).isDefinedAt)
        val input = Stream("1.one", cancelMessage).map(message)

        assert(input.through(cancellable.pipe).size() == 0)
      }

      "evaluates cancellation effect" in {
        var cancelled = false
        val cancelMessage = "cancel"

        val scenario: Scenario[IO, Unit] =
          for {
            _ <- Scenario.expect(any)
            _ <- Scenario.expect(any)
          } yield ()

        val cancellable = scenario.cancelWith(textMessage.matching(cancelMessage).isDefinedAt) { _ =>
          IO { cancelled = true }
        }

        val input = Stream("1.one", cancelMessage).map(message)

        input.through(cancellable.pipe).run()
        assert(cancelled)
      }
    }

    "tolerate" - {
      "skips up to N elements if they don't match" in {
        val n = 5
        val scenario: Scenario[IO, TextMessage] =
          for {
            _ <- Scenario.expect(any)
            s <- Scenario.expect(textMessage.endingWith("fire")).tolerateN(n)(_ => IO.unit)
          } yield s
        val input = Stream("any") ++ Stream("").repeatN(n) ++ Stream("2.fire")

        assert(input.map(message).through(scenario.pipe).value().text.startsWith("2"))
      }

      "doesn't skip the element if it matches" in {
        val scenario: Scenario[IO, String] =
          Scenario.expect(textMessage.endingWith("fire").map(_.text)).tolerate(_ => IO.unit)
        val input = Stream("1.fire", "2.fire").map(message)

        assert(input.through(scenario.pipe).value().startsWith("1"))
      }

      "can skip all unmatching elements" in {
        val scenario: Scenario[IO, TextMessage] =
          for {
            _ <- Scenario.expect(any)
            s <- Scenario.expect(textMessage.endingWith("fire")).tolerateAll(_ => IO.unit)
          } yield s
        val input = Stream("any") ++ Stream("").repeatN(1000) ++ Stream("2.fire")

        assert(input.map(message).through(scenario.pipe).value().text.startsWith("2"))
      }

      "evaluates provided effect each time it skips the element" in {
        var counter = 0
        val scenario: Scenario[IO, Unit] =
          for {
            _ <- Scenario.expect(any)
            _ <- Scenario.expect(textMessage.endingWith("fire")).tolerateAll(_ => IO { counter += 1 })
          } yield ()

        val input = Stream("any", "1", "2", "fire").map(message)
        input.through(scenario.pipe).run()

        assert(counter == 2)
      }
    }

    "interpetation" - {
      "is stack safe" in {
        def stack[F[_]](n: Long): Scenario[F, Long] = {
          def bind(n: Long, sc: Scenario[F, Long]): Scenario[F, Long] =
            if (n <= 0) sc
            else bind(n - 1, sc.flatMap(l => Scenario.pure(l + 1)))

          bind(n, Scenario.pure(0))
        }

        val n = 100000L
        assert(Stream.empty.through(stack[IO](n).pipe).value() == n)
      }

      "propagates input stream failure in a safe way" in {
        case class Error(s: String) extends Throwable

        val input: Stream[IO, TelegramMessage] =
          Stream(Right(""), Left(Error("test")), Right("2"), Right("3")).evalMap {
            case Right(i)    => IO.pure(message(i))
            case Left(error) => IO.raiseError(error)
          }

        val scenario: Scenario[IO, Unit] =
          for {
            _ <- Scenario.expect(any)
            _ <- Scenario.expect(any)
          } yield ()

        input.through(scenario.attempt.pipe).run()
      }
    }

    "flatMap" - {
      "composes scenarios together" in {
        val scenario: Scenario[IO, Unit] =
          for {
            _ <- Scenario.expect(textMessage.matching("one"))
            _ <- Scenario.expect(textMessage.matching("two"))
          } yield ()

        val input = Stream("one", "two").map(message)
        assert(input.through(scenario.pipe).size() == 1)
      }
    }
  }
}
