package canoe.scenarios

import cats.Id
import cats.effect.IO
import fs2.{Pure, Stream}
import org.scalatest.FunSuite

class ScenarioSpec extends FunSuite {

  implicit class IOStreamOps[A](stream: Stream[IO, A]) {
    def toList(): List[A] = stream.compile.toList.unsafeRunSync()

    def value(): A = toList().head

    def size(): Int = toList().size

    def run(): Unit = stream.compile.drain.unsafeRunSync()
  }

  val expected: String = "fire"

  val predicate: String => Boolean = _.endsWith(expected)


  test("Start >>= Next") {
    val scenario: Scenario[Pure, String, String] =
      for {
        m <- Scenario.start[Pure, String](_.endsWith("one"))
        _ <- Scenario.next[Pure, String](_.endsWith("two"))
      } yield m

    val input = Stream("one", "two")

    assert(input.through(scenario).toList().size == 1)
  }

  test("Scenario doesn't ignore the element which is mismatched") {
    val scenario: Scenario[Pure, String, String] =
      for {
        m <- Scenario.start[Pure, String](_.endsWith("one"))
        _ <- Scenario.next[Pure, String](_.endsWith("two"))
      } yield m

    val input = Stream("1.one", "2.one", "3.two")

    assert(input.through(scenario).toList().head.take(1) == "2")
  }

  test("Scenario can be cancelled while it's in progress") {
    val cancelToken = "cancel"

    val scenario: Scenario[Pure, String, String] =
      (for {
        m <- Scenario.start[Pure, String](_.endsWith("one"))
        _ <- Scenario.next[Pure, String](_ => true)
      } yield m).cancelOn(_ == cancelToken)

    val input = Stream("1.one", cancelToken, "any")

    assert(input.through(scenario).size == 0)
  }

  test("Scenario evaluates cancellation function when it is cancelled") {
    var cancelledWith = ""
    val cancelToken = "cancel"

    val scenario: Scenario[IO, String, String] =
      (for {
        m <- Scenario.start[IO, String](_.endsWith("one"))
        _ <- Scenario.next[IO, String](_ => true)
      } yield m).cancelWith[String](_ == cancelToken)(m => IO { cancelledWith = m })

    val input = Stream("1.one", cancelToken, "any")
    input.through(scenario).run()

    assert(cancelledWith == cancelToken)
  }

  test("Scenario.start needs at least one message") {
    val scenario: Scenario[Pure, String, String] = Scenario.start(predicate)
    val input = Stream.empty

    assert(input.through(scenario).toList().isEmpty)
  }

  test("Scenario.start returns all matched occurrences") {
    val scenario: Scenario[Pure, String, String] = Scenario.start(predicate)
    val input = Stream(
      s"1.$expected",
      s"1.$expected",
      s"1.",
      s"2.$expected"
    )

    assert(input.through(scenario).size() == input.toList().count(predicate))
  }


  test("Scenario.next needs at least one message") {
    val scenario: Scenario[Pure, String, String] = Scenario.next(predicate)
    val input = Stream.empty

    assert(input.through(scenario).toList().isEmpty)
  }

  test("Scenario.next matches only the first message") {
    val scenario: Scenario[Pure, String, String] = Scenario.next(predicate)

    val input = Stream(s"1.$expected", s"2.$expected")

    val results = input.through(scenario).toList()
    assert(results.size == 1)
    assert(results.head.startsWith("1"))
  }

  test("Scenario.next uses provided predicate to match the result") {
    val scenario: Scenario[Pure, String, String] = Scenario.next(predicate)
    val input = Stream("")

    assert(input.through(scenario).toList().isEmpty)
  }


  test("Scenario.next#tolerate doesn't skip the element if it matches") {
    val scenario: Scenario[Id, String, String] =
      Scenario.next(predicate).tolerate(_ => (): Id[Unit])

    val input = Stream(s"1.$expected", s"2.$expected")

    assert(input.through(scenario).covaryId[IO].toList().head.startsWith("1"))
  }

  test("Scenario.next#tolerate skips the element if it doesn't match") {
    val scenario: Scenario[Id, String, String] =
      Scenario.next(predicate).tolerate(_ => (): Id[Unit])

    val input = Stream("1", s"2.$expected")

    assert(input.through(scenario).covaryId[IO].toList().head.startsWith("2"))
  }

  test("Scenario.next#tolerateN skips up to N elements if they don't match") {
    val n = 5
    val scenario: Scenario[Id, String, String] =
      Scenario.next(predicate).tolerateN(n)(_ => (): Id[Unit])

    val input = Stream("").repeatN(5) ++ Stream(s"2.$expected")

    assert(input.through(scenario).covaryId[IO].toList().head.startsWith("2"))
  }

  test("Scenario.eval doesn't consume any message") {
    val scenario: Scenario[Id, Unit, Unit] = Scenario.eval((): Id[Unit])
    val input: Stream[Pure, Unit] = Stream.empty

    assert(input.through(scenario).covaryId[IO].size == 1)
  }

  test("Scenario.eval evaluates effect") {
    var evaluated = false
    val scenario: Scenario[IO, Unit, Unit] = Scenario.eval(IO { evaluated = true })
    val input: Stream[Pure, Unit] = Stream.empty

    input.through(scenario).run()

    assert(evaluated)
  }

  test("Action evaluates value in an effect") {
    val scenario: Scenario[IO, Unit, Int] = Scenario.eval(IO.pure(1))
    val input: Stream[Pure, Unit] = Stream.empty

    assert(input.through(scenario).value() == 1)
  }

  test("Action evaluates effect only once") {
    var times = 0
    val scenario: Scenario[IO, Unit, Unit] = Scenario.eval(IO { times = times + 1 })
    val input: Stream[Pure, Unit] = Stream.empty

    input.through(scenario).run()
    assert(times == 1)
  }
}
