package canoe.api.matching

import canoe.TestIO._
import cats.effect.IO
import fs2.Stream
import org.scalatest.funsuite.AnyFunSuite

class EpisodeSpec extends AnyFunSuite {

  val expected: String = "fire"

  val predicate: String => Boolean = _.endsWith(expected)

  test("Episode.First >>= Episode.Next") {
    val episode: Episode[fs2.Pure, String, String] =
      for {
        m <- Episode.First[fs2.Pure, String](_.endsWith("one"))
        _ <- Episode.Next[fs2.Pure, String](_.endsWith("two"))
      } yield m

    val input = Stream("one", "two")

    assert(input.through(episode.matching).toList().size == 1)
  }

  test("Episode doesn't ignore the element which is mismatched") {
    val episode: Episode[fs2.Pure, String, String] =
      for {
        m <- Episode.First[fs2.Pure, String](_.endsWith("one"))
        _ <- Episode.Next[fs2.Pure, String](_.endsWith("two"))
      } yield m

    val input = Stream("1.one", "2.one", "3.two")

    assert(input.through(episode.matching).toList().head.take(1) == "2")
  }

  test("Episode can be cancelled while it's in progress") {
    val cancelToken = "cancel"

    val episode: Episode[fs2.Pure, String, String] =
      (for {
        m <- Episode.First[fs2.Pure, String](_.endsWith("one"))
        _ <- Episode.Next[fs2.Pure, String](_ => true)
      } yield m).cancelOn(_ == cancelToken)

    val input = Stream("1.one", cancelToken, "any")

    assert(input.through(episode.matching).size == 0)
  }

  test("Episode evaluates cancellation function when it is cancelled") {
    var cancelledWith = ""
    val cancelToken = "cancel"

    val episode: Episode[IO, String, String] =
      (for {
        m <- Episode.First[IO, String](_.endsWith("one"))
        _ <- Episode.Next[IO, String](_ => true)
      } yield m).cancelWith[String](_ == cancelToken)(m => IO { cancelledWith = m })

    val input = Stream("1.one", cancelToken, "any")
    input.through(episode.matching).run()

    assert(cancelledWith == cancelToken)
  }

  test("Episode.First needs at least one message") {
    val episode: Episode[fs2.Pure, String, String] = Episode.First(predicate)
    val input = Stream.empty

    assert(input.through(episode.matching).toList().isEmpty)
  }

  test("Episode.First returns all matched occurrences") {
    val episode: Episode[fs2.Pure, String, String] = Episode.First(predicate)
    val input = Stream(
      s"1.$expected",
      s"1.$expected",
      s"1.",
      s"2.$expected"
    )

    assert(input.through(episode.matching).size() == input.toList().count(predicate))
  }

  test("Episode.Next needs at least one message") {
    val episode: Episode[fs2.Pure, String, String] = Episode.Next(predicate)
    val input = Stream.empty

    assert(input.through(episode.matching).toList().isEmpty)
  }

  test("Episode.Next matches only the first message") {
    val episode: Episode[fs2.Pure, String, String] = Episode.Next(predicate)

    val input = Stream(s"1.$expected", s"2.$expected")

    val results = input.through(episode.matching).toList()
    assert(results.size == 1)
    assert(results.head.startsWith("1"))
  }

  test("Episode.Next uses provided predicate to match the result") {
    val episode: Episode[fs2.Pure, String, String] = Episode.Next(predicate)
    val input = Stream("")

    assert(input.through(episode.matching).toList().isEmpty)
  }

  test("Episode.Next#tolerate doesn't skip the element if it matches") {
    val episode: Episode[IO, String, String] =
      Episode.Next(predicate).tolerateN(1)(_ => IO.unit)

    val input = Stream(s"1.$expected", s"2.$expected")

    assert(input.through(episode.matching).toList().head.startsWith("1"))
  }

  test("Episode.Next#tolerateN skips up to N elements if they don't match") {
    val n = 5
    val episode: Episode[IO, String, String] =
      Episode.Next(predicate).tolerateN(n)(_ => IO.unit)

    val input = Stream("").repeatN(5) ++ Stream(s"2.$expected")

    assert(input.through(episode.matching).toList().head.startsWith("2"))
  }

  test("Episode.Eval doesn't consume any message") {
    val episode: Episode[IO, Unit, Unit] = Episode.Eval(IO.unit)
    val input: Stream[fs2.Pure, Unit] = Stream.empty

    assert(input.through(episode.matching).size == 1)
  }

  test("Episode.Eval evaluates effect") {
    var evaluated = false
    val episode: Episode[IO, Unit, Unit] = Episode.Eval(IO { evaluated = true })
    val input: Stream[fs2.Pure, Unit] = Stream.empty

    input.through(episode.matching).run()

    assert(evaluated)
  }

  test("Episode.Eval evaluates value in an effect") {
    val episode: Episode[IO, Unit, Int] = Episode.Eval(IO.pure(1))
    val input: Stream[fs2.Pure, Unit] = Stream.empty

    assert(input.through(episode.matching).value() == 1)
  }

  test("Episode.Eval evaluates effect only once") {
    var times = 0
    val episode: Episode[IO, Unit, Unit] = Episode.Eval(IO { times = times + 1 })
    val input: Stream[fs2.Pure, Unit] = Stream.empty

    input.through(episode.matching).run()
    assert(times == 1)
  }

  test("Episode.Eval propagates error raised in underlying value") {
    case class Error(s: String) extends Throwable
    val episode: Episode[IO, Unit, Int] = Episode.Eval(IO.raiseError(Error("test")))

    assertThrows[Error](Stream.empty.through(episode.matching).run())
  }

  test("Episode.handleErrorWith transforms into provided substitute in case of error") {
    case class Error(s: String) extends Throwable
    val episode: Episode[IO, Unit, Int] =
      Episode
        .Eval[IO, Unit, Int](IO.raiseError(Error("test")))
        .flatMap(_ => Episode.Eval[IO, Unit, Int](IO.pure(-1)))
        .handleErrorWith(_ => Episode.Eval[IO, Unit, Int](IO.pure(12)))

    assert(Stream.empty.through(episode.matching).value() == 12)
  }

  test("Episode.attempt wraps exception in left part of Either") {
    case class Error(s: String) extends Throwable
    val error = Error("test")

    val episode: Episode[IO, Unit, Unit] = Episode.Eval(IO.raiseError(error))
    assert(Stream.empty.through(episode.attempt.matching).value() == Left(error))
  }

  test("Episode.attempt wraps result in right part of Either") {
    val episode: Episode[IO, Unit, Int] = Episode.Eval(IO.pure(12))
    assert(Stream.empty.through(episode.attempt.matching).value() == Right(12))
  }

  test("should not happen") {
    case class Error(s: String) extends Throwable
    val episode: Episode[IO, Unit, Error] = Episode.Eval(IO.pure(Error("test")))
    assert(Stream.empty.through(episode.matching).value() == Error("test"))
  }
}
