package canoe.api

import canoe.TestIO._
import canoe.models.PrivateChat
import canoe.models.messages.{TelegramMessage, TextMessage}
import canoe.syntax._
import cats.effect.IO
import fs2.Stream
import org.scalatest.propspec.AnyPropSpec

class ScenarioSpec extends AnyPropSpec {

  private def message(s: String): TextMessage =
    TextMessage(-1, PrivateChat(-1, None, None, None), -1, s)

  property("Scenario.start consumes at least one message") {
    val scenario: Scenario[IO, TextMessage] = Scenario.start(command("fire"))
    val input = Stream.empty

    assert(input.through(scenario.pipe).toList().isEmpty)
  }

  property("Scenario.first returns all matched occurrences") {
    val trigger = "fire"
    val scenario: Scenario[IO, TextMessage] = Scenario.start(textMessage.matching(trigger))

    val input = Stream(
      trigger,
      trigger,
      "dasd",
      trigger
    ).map(message)

    assert(input.through(scenario.pipe).size() == input.toList().count(_.text == trigger))
  }

  property("Scenario.next consumes at least one message") {
    val scenario: Scenario[IO, TextMessage] = Scenario.next(command("fire"))
    val input = Stream.empty

    assert(input.through(scenario.pipe).toList().isEmpty)
  }

  property("Scenario.next matches only the first message") {
    val trigger = "fire"
    val scenario: Scenario[IO, TextMessage] = Scenario.start(textMessage.endingWith(trigger))

    val input = Stream(
      s"1.$trigger",
      s"2.$trigger"
    ).map(message)

    assert(input.through(scenario.pipe).value().text.startsWith("1"))
  }

  property("Scenario.next uses provided predicate to match the result") {
    val scenario: Scenario[IO, TextMessage] = Scenario.start(textMessage.endingWith("fire"))
    val input = Stream("").map(message)

    assert(input.through(scenario.pipe).toList().isEmpty)
  }

  property("Scenario.eval doesn't consume any message") {
    val scenario: Scenario[IO, Unit] = Scenario.eval(IO.unit)
    val input = Stream.empty

    assert(input.through(scenario.pipe).size() == 1)
  }

  property("Scenario.eval evaluates value in the effect") {
    val scenario: Scenario[IO, Int] = Scenario.eval(IO.pure(12))

    assert(Stream.empty.through(scenario.pipe).value() == 12)
  }

  property("Scenario.eval evaluates the effect when it is run") {
    var evaluated = false
    val scenario: Scenario[IO, Unit] = Scenario.eval(IO { evaluated = true })

    Stream.empty.through(scenario.pipe).run()
    assert(evaluated)
  }

  property("Scenario.eval evaluates the effect only once") {
    var counter = 0
    val scenario: Scenario[IO, Unit] = Scenario.eval(IO { counter += 1 })
    Stream.empty.through(scenario.pipe).run()

    assert(counter == 1)
  }

  property("Scenario.eval propagates error raised in underlying value") {
    case class Error(s: String) extends Throwable
    val scenario: Scenario[IO, Unit] = Scenario.eval(IO.raiseError(Error("test")))

    assertThrows[Error](Stream.empty.through(scenario.pipe).run())
  }

  property("Scenario.raiseError interrupts the flow of the scenario") {
    case class Error(s: String) extends Throwable
    val scenario: Scenario[IO, Int] = Scenario.raiseError(Error("test")).map(_ => 12)

    assertThrows[Error](Stream.empty.through(scenario.pipe).value())
  }

  property("Scenario.handleErrorWith transforms into provided substitute in case of error") {
    case class Error(s: String) extends Throwable
    val scenario: Scenario[IO, Int] =
      Scenario
        .raiseError(Error("test"))
        .flatMap(_ => Scenario.eval(IO.pure(-1)))
        .handleErrorWith(_ => Scenario.eval(IO.pure(12)))

    assert(Stream.empty.through(scenario.pipe).value() == 12)
  }

  property("Scenario.attempt wraps exception in left part of Either") {
    case class Error(s: String) extends Throwable
    val error = Error("test")

    val scenario: Scenario[IO, Unit] = Scenario.raiseError(error)
    assert(Stream.empty.through(scenario.attempt.pipe).value() == Left(error))
  }

  property("Scenario.attempt wraps result in right part of Either") {
    val scenario: Scenario[IO, Int] = Scenario.eval(IO.pure(12))
    assert(Stream.empty.through(scenario.attempt.pipe).value() == Right(12))
  }

  property("flatMap composes scenarios together") {
    val scenario: Scenario[IO, Unit] =
      for {
        _ <- Scenario.start(textMessage.matching("one"))
        _ <- Scenario.next(textMessage.matching("two"))
      } yield ()

    val input = Stream("one", "two").map(message)
    assert(input.through(scenario.pipe).size() == 1)
  }

  property("Scenario doesn't ignore the element which is mismatched") {
    val scenario: Scenario[IO, String] =
      for {
        m <- Scenario.start(textMessage.endingWith("one"))
        _ <- Scenario.next(textMessage.endingWith("two"))
      } yield m.text
    val input = Stream("1.one", "2.one", "3.two").map(message)

    assert(input.through(scenario.pipe).value().startsWith("2"))
  }

  property("Scenario can be cancelled while it's in progress") {
    val cancelMessage = "cancel"
    val scenario: Scenario[IO, Unit] =
      for {
        _ <- Scenario.start(any)
        _ <- Scenario.next(any)
      } yield ()

    val cancellable = scenario.cancelOn(textMessage.matching(cancelMessage))
    val input = Stream("1.one", cancelMessage).map(message)

    assert(input.through(cancellable.pipe).size() == 0)
  }

  property("Scenario evaluates cancellation function when it is cancelled") {
    var cancelled = false
    val cancelMessage = "cancel"

    val scenario: Scenario[IO, Unit] =
      for {
        _ <- Scenario.start(any)
        _ <- Scenario.next(any)
      } yield ()

    val cancellable = scenario.cancelWith(textMessage.matching(cancelMessage)) { _ =>
      IO { cancelled = true }
    }

    val input = Stream("1.one", cancelMessage).map(message)

    input.through(cancellable.pipe).run()
    assert(cancelled)
  }

  property("tolerate skips up to N elements if they don't match") {
    val n = 5
    val scenario: Scenario[IO, String] = Scenario.next(textMessage.endingWith("fire").map(_.text))
    val input = Stream("").repeatN(n) ++ Stream(s"2.fire")
    val tolerating = scenario.tolerateN(n)(_ => IO.unit)

    assert(input.map(message).through(tolerating.pipe).value().startsWith("2"))
  }

  property("tolerate doesn't skip the element if it matches") {
    val scenario: Scenario[IO, String] =
      Scenario.next(textMessage.endingWith("fire").map(_.text)).tolerate(_ => IO.unit)
    val input = Stream("1.fire", "2.fire").map(message)

    assert(input.through(scenario.pipe).value().startsWith("1"))
  }

  property("tolerate evaluates provided effect each time it skips the element") {
    var counter = 0
    val scenario: Scenario[IO, String] = Scenario.next(textMessage.endingWith("fire").map(_.text))
    val input = Stream("1", "2", "fire").map(message)
    val tolerating = scenario.tolerateAll(_ => IO { counter += 1 })
    input.through(tolerating.pipe).run()

    assert(counter == 2)
  }

  property("Scenario is stack safe during the interpretation") {
    def stack[F[_]](n: Long): Scenario[F, Long] = {
      def bind(n: Long, sc: Scenario[F, Long]): Scenario[F, Long] =
        if (n <= 0) sc
        else bind(n - 1, sc.flatMap(l => Scenario.pure(l + 1)))

      bind(n, Scenario.pure(0))
    }

    val n = 100000L
    assert(Stream.empty.through(stack[IO](n).pipe).value() == n)
  }

  property("Scenario doesn't throw exception during the interpretation") {
    case class Error(s: String) extends Throwable

    val input: Stream[IO, TelegramMessage] =
      Stream(Right(""), Left(Error("test")), Right("2"), Right("3")).evalMap {
        case Right(i)    => IO.pure(message(i))
        case Left(error) => IO.raiseError(error)
      }

    val scenario: Scenario[IO, Unit] =
      for {
        _ <- Scenario.start(any)
        _ <- Scenario.next(any)
      } yield ()

    input.through(scenario.attempt.pipe).run()
  }
}
