package canoe.api

import canoe.IOSpec
import canoe.models.PrivateChat
import canoe.models.messages.{TelegramMessage, TextMessage}
import canoe.syntax._
import cats.effect.IO
import fs2.Stream
import org.scalatest.freespec.AsyncFreeSpec

class ScenarioSpec extends AsyncFreeSpec with IOSpec {
  private def message(s: String): TextMessage =
    TextMessage(-1, PrivateChat(-1, None, None, None), -1, s)

  "Scenario" - {
    "expect" - {
      "consumes at least one message" in {
        val scenario: Scenario[IO, TextMessage] = Scenario.expect(command("fire"))
        val input = Stream.empty

        for {
          res <- input.through(scenario.pipe).compile.toList
          asr <- IO(assert(res.isEmpty))
        } yield asr
      }

      "uses provided predicate to match the result" in {
        val scenario: Scenario[IO, TextMessage] = Scenario.expect(textMessage.endingWith("fire"))
        val input = Stream("").map(message)

        for {
          res <- input.through(scenario.pipe).compile.toList
          asr <- IO(assert(res.isEmpty))
        } yield asr
      }

      "halts the stream if the first element was not matched" in {
        val trigger = "fire"
        val scenario: Scenario[IO, TextMessage] = Scenario.expect(textMessage.matching(trigger))

        val input = Stream("not_matched", trigger).map(message)

        for {
          res <- input.through(scenario.pipe).compile.toList
          asr <- IO(assert(res.isEmpty))
        } yield asr
      }

      "checks only the first message" in {
        val trigger = "fire"
        val scenario: Scenario[IO, TextMessage] = Scenario.expect(textMessage.endingWith(trigger))

        val input = Stream(
          s"1.$trigger",
          s"2.$trigger"
        ).map(message)

        for {
          res <- input.through(scenario.pipe).compile.toList
          asr <- IO(assert(res == input.toList.take(1)))
        } yield asr
      }
    }

    "eval" - {
      "doesn't consume any message" in {
        val scenario: Scenario[IO, Unit] = Scenario.eval(IO.unit)
        val input = Stream.empty

        for {
          res <- input.through(scenario.pipe).compile.count
          asr <- IO(assert(res == 1))
        } yield asr
      }

      "evaluates value in the effect" in {
        val scenario: Scenario[IO, Int] = Scenario.eval(IO.pure(12))

        for {
          res <- Stream.empty.through(scenario.pipe).head.compile.lastOrError
          asr <- IO(assert(res == 12))
        } yield asr
      }

      "evaluates effect once" in {
        var counter = 0
        val scenario: Scenario[IO, Unit] = Scenario.eval(IO(counter += 1))

        for {
          _   <- Stream.empty.through(scenario.pipe).compile.drain
          asr <- IO(assert(counter == 1))
        } yield asr
      }

      "propagates error raised in underlying value" in {
        case class Error(s: String) extends Throwable
        val error = Error("test")
        val scenario: Scenario[IO, Unit] = Scenario.eval(IO.raiseError(Error("test")))

        for {
          res <- Stream.empty.through(scenario.pipe).compile.drain.attempt
          asr <- IO(assert(res == Left(error)))
        } yield asr
      }
    }

    "raiseError" - {
      "interrupts the flow of the scenario" in {
        case class Error(s: String) extends Throwable
        val error = Error("test")
        val scenario: Scenario[IO, Int] = Scenario.raiseError(error).map(_ => 12)

        for {
          res <- Stream.empty.through(scenario.pipe).compile.drain.attempt
          asr <- IO(assert(res == Left(error)))
        } yield asr
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

        for {
          res <- Stream.empty.through(scenario.pipe).head.compile.lastOrError
          asr <- IO(assert(res == 12))
        } yield asr
      }
    }

    "attempt" - {
      "wraps exception in left part of Either" in {
        case class Error(s: String) extends Throwable
        val error = Error("test")

        val scenario: Scenario[IO, Unit] = Scenario.raiseError(error)

        for {
          res <- Stream.empty.through(scenario.attempt.pipe).head.compile.lastOrError
          asr <- IO(assert(res == Left(error)))
        } yield asr
      }

      "wraps result in right part of Either" in {
        val scenario: Scenario[IO, Int] = Scenario.eval(IO.pure(12))
        for {
          res <- Stream.empty.through(scenario.attempt.pipe).head.compile.lastOrError
          asr <- IO(assert(res == Right(12)))
        } yield asr
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

        val cancellable = scenario.stopOn(textMessage.matching(cancelMessage).isDefinedAt)
        val input = Stream("1.one", cancelMessage).map(message)

        for {
          res <- input.through(cancellable.pipe).compile.count
          asr <- IO(assert(res == 12))
        } yield asr
      }

      "evaluates cancellation effect" in {
        var cancelled = false
        val cancelMessage = "cancel"

        val scenario: Scenario[IO, Unit] =
          for {
            _ <- Scenario.expect(any)
            _ <- Scenario.expect(any)
          } yield ()

        val cancellable = scenario.stopWith(textMessage.matching(cancelMessage).isDefinedAt) { _ =>
          IO { cancelled = true }
        }

        val input = Stream("1.one", cancelMessage).map(message)

        for {
          _   <- input.through(cancellable.pipe).compile.drain
          asr <- IO(assert(cancelled))
        } yield asr
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

        for {
          msg <- input.map(message).through(scenario.pipe).head.compile.lastOrError
          asr <- IO(assert(msg.text.startsWith("2")))
        } yield asr
      }

      "doesn't skip the element if it matches" in {
        val scenario: Scenario[IO, String] =
          Scenario.expect(textMessage.endingWith("fire")).map(_.text).tolerate(_ => IO.unit)
        val input = Stream("1.fire", "2.fire").map(message)

        for {
          string <- input.through(scenario.pipe).head.compile.lastOrError
          asr    <- IO(assert(string.startsWith("2")))
        } yield asr
      }

      "can skip all unmatching elements" in {
        val scenario: Scenario[IO, TextMessage] =
          for {
            _ <- Scenario.expect(any)
            s <- Scenario.expect(textMessage.endingWith("fire")).tolerateAll(_ => IO.unit)
          } yield s
        val input = Stream("any") ++ Stream("").repeatN(1000) ++ Stream("2.fire")

        for {
          message <- input.map(message).through(scenario.pipe).head.compile.lastOrError
          asr     <- IO(assert(message.text.startsWith("2")))
        } yield asr
      }

      "evaluates provided effect each time it skips the element" in {
        var counter = 0
        val scenario: Scenario[IO, Unit] =
          for {
            _ <- Scenario.expect(any)
            _ <- Scenario.expect(textMessage.endingWith("fire")).tolerateAll(_ => IO(counter += 1))
          } yield ()

        val input = Stream("any", "1", "2", "fire").map(message)

        for {
          _   <- input.through(scenario.pipe).compile.drain
          asr <- IO(assert(counter == 2))
        } yield asr
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

        for {
          res <- Stream.empty.through(stack[IO](n).pipe).head.compile.lastOrError
          asr <- IO(assert(res == n))
        } yield asr
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

        for {
          _ <- input.through(scenario.attempt.pipe).compile.drain
        } yield succeed // implicit assertion that program doesn't fail
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
        for {
          count <- input.through(scenario.pipe).compile.count
          asr   <- IO(assert(count == 1))
        } yield asr
      }
    }
  }
}
