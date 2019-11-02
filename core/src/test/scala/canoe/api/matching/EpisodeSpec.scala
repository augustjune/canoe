package canoe.api.matching

import canoe.TestIO._
import cats.effect.IO
import cats.syntax.functor._
import fs2.Stream
import org.scalatest.funsuite.AnyFunSuite

class EpisodeSpec extends AnyFunSuite {

  val expected: String = "fire"

  val predicate: String => Boolean = _.endsWith(expected)

  test("Episode.First >>= Episode.Next") {
    val episode: Episode[IO, String, String] =
      for {
        m <- Episode.First[IO, String](_.endsWith("one"))
        _ <- Episode.Next[IO, String](_.endsWith("two"))
      } yield m

    val input = Stream("one", "two")

    assert(input.through(episode.matching).size() == 1)
  }

  test("Episode doesn't ignore the element which is mismatched") {
    val episode: Episode[IO, String, String] =
      for {
        m <- Episode.First[IO, String](_.endsWith("one"))
        _ <- Episode.Next[IO, String](_.endsWith("two"))
      } yield m

    val input = Stream("1.one", "2.one", "3.two")

    assert(input.through(episode.matching).value().startsWith("2"))
  }

  test("Episode can be cancelled while it's in progress") {
    val cancelToken = "cancel"

    val episode: Episode[IO, String, String] =
      for {
        m <- Episode.First[IO, String](_.endsWith("one"))
        _ <- Episode.Next[IO, String](_ => true)
      } yield m

    val cancellable: Episode[IO, String, String] =
      Episode.Cancellable(episode, _ == cancelToken, None)

    val input = Stream("1.one", cancelToken, "any")

    assert(input.through(cancellable.matching).size == 0)
  }

  test("Episode evaluates cancellation function when it is cancelled") {
    var cancelledWith = ""
    val cancelToken = "cancel"

    val episode: Episode[IO, String, String] =
      for {
        m <- Episode.First[IO, String](_.endsWith("one"))
        _ <- Episode.Next[IO, String](_ => true)
      } yield m

    val cancellable: Episode[IO, String, String] =
      Episode.Cancellable(episode, _ == cancelToken, Some(m => IO { cancelledWith = m }))

    val input = Stream("1.one", cancelToken, "any")
    input.through(cancellable.matching).run()

    assert(cancelledWith == cancelToken)
  }

  test("Episode.First needs at least one message") {
    val episode: Episode[IO, String, String] = Episode.First(predicate)
    val input = Stream.empty

    assert(input.through(episode.matching).toList().isEmpty)
  }

  test("Episode.First returns all matched occurrences") {
    val episode: Episode[IO, String, String] = Episode.First(predicate)
    val input = Stream(
      s"1.$expected",
      s"1.$expected",
      s"1.",
      s"2.$expected"
    )

    assert(input.through(episode.matching).size() == input.toList().count(predicate))
  }

  test("Episode.Next needs at least one message") {
    val episode: Episode[IO, String, String] = Episode.Next(predicate)
    val input = Stream.empty

    assert(input.through(episode.matching).toList().isEmpty)
  }

  test("Episode.Next matches only the first message") {
    val episode: Episode[IO, String, String] = Episode.Next(predicate)

    val input = Stream(s"1.$expected", s"2.$expected")

    val results = input.through(episode.matching).toList()
    assert(results.size == 1)
    assert(results.head.startsWith("1"))
  }

  test("Episode.Next uses provided predicate to match the result") {
    val episode: Episode[IO, String, String] = Episode.Next(predicate)
    val input = Stream("")

    assert(input.through(episode.matching).toList().isEmpty)
  }

  test("Episode.Next#tolerateN skips up to N elements if they don't match") {
    val n = 5
    val episode: Episode[IO, String, String] =
      Episode.Tolerate(Episode.Next(predicate), Some(n), _ => IO.unit)

    val input = Stream("").repeatN(5) ++ Stream(s"2.$expected")

    assert(input.through(episode.matching).value().startsWith("2"))
  }

  test("Episode.Next#tolerate doesn't skip the element if it matches") {
    val episode: Episode[IO, String, String] =
      Episode.Tolerate(Episode.Next(predicate), Some(1), _ => IO.unit)

    val input = Stream(s"1.$expected", s"2.$expected")

    assert(input.through(episode.matching).value().startsWith("1"))
  }

  test("Episode.Eval doesn't consume any message") {
    val episode: Episode[IO, Unit, Unit] = Episode.Eval(IO.unit)
    val input: Stream[IO, Unit] = Stream.empty

    assert(input.through(episode.matching).size == 1)
  }

  test("Episode.Eval evaluates effect") {
    var evaluated = false
    val episode: Episode[IO, Unit, Unit] = Episode.Eval(IO { evaluated = true })
    val input: Stream[IO, Unit] = Stream.empty

    input.through(episode.matching).run()

    assert(evaluated)
  }

  test("Episode.Eval evaluates value in an effect") {
    val episode: Episode[IO, Unit, Int] = Episode.Eval(IO.pure(1))
    val input: Stream[IO, Unit] = Stream.empty

    assert(input.through(episode.matching).value() == 1)
  }

  test("Episode.Eval evaluates effect only once") {
    var times = 0
    val episode: Episode[IO, Unit, Unit] = Episode.Eval(IO { times = times + 1 })
    val input: Stream[IO, Unit] = Stream.empty

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

    val failing: Episode[IO, Unit, Int] = Episode.RaiseError[IO](Error("test")).as(-1)
    val recover: Episode[IO, Any, Int] = Episode.Pure(12)

    val episode = Episode.Protected(failing, _ => recover)

    assert(Stream.empty.through(episode.matching).value() == 12)
  }

  test("Episode is stack safe during the interpretation") {
    def stack(n: Long): Episode[IO, Any, Long] = {
      def bind(n: Long, ep: Episode[IO, Any, Long]): Episode[IO, Any, Long] =
        if (n <= 0) ep
        else bind(n - 1, ep.flatMap(l => Episode.Pure(l + 1)))

      bind(n, Episode.Pure[IO, Long](0L))
    }

    val n = 100000L
    assert(Stream.empty.through(stack(n).matching).value() == n)
  }
}
