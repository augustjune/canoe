package canoe.api.matching

import canoe.TestIO._
import cats.effect.IO
import cats.effect.concurrent.Ref
import cats.syntax.all._
import fs2.Stream
import org.scalatest.funsuite.AnyFunSuite

class EpisodeSpec extends AnyFunSuite {
  val expected: String = "fire"

  val predicate: String => Boolean = _.endsWith(expected)

  test("Episode.Next >>= Episode.Next") {
    val episode: Episode[IO, String, String] =
      for {
        m <- Episode.Next[IO, String](_.endsWith("one"))
        _ <- Episode.Next[IO, String](_.endsWith("two"))
      } yield m

    val input = Stream("one", "two")

    assert(input.through(episode.matching).size() == 1)
  }

  test("Episode can be cancelled while it's in progress") {
    val cancelToken = "cancel"

    val episode: Episode[IO, String, String] =
      for {
        m <- Episode.Next[IO, String](_.endsWith("one"))
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
        m <- Episode.Next[IO, String](_.endsWith("one"))
        _ <- Episode.Next[IO, String](_ => true)
      } yield m

    val cancellable: Episode[IO, String, String] =
      Episode.Cancellable(episode, _ == cancelToken, Some(m => IO { cancelledWith = m }))

    val input = Stream("1.one", cancelToken, "any")
    input.through(cancellable.matching).run()

    assert(cancelledWith == cancelToken)
  }

  test("Episode.First needs at least one message") {
    val episode: Episode[IO, String, String] = Episode.Next(predicate)
    val input = Stream.empty

    assert(input.through(episode.matching).toList().isEmpty)
  }

  test("Episode.First halts the stream if the first element was not matched") {
    val episode: Episode[IO, String, String] = Episode.Next(predicate)
    val input = Stream("not_matched", expected)

    assert(input.through(episode.matching).toList().isEmpty)
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

  test("Tolerate is ignored if the first Next was missmatched") {
    val episode: Episode[IO, String, String] =
      Episode.Tolerate(Episode.Next(predicate), None, _ => IO.unit)

    val input = Stream("", expected)
    assert(input.through(episode.matching).toList().isEmpty)
  }

  test("Tolerate is not ignored if not the second Next was missmatched") {
    val original: Episode[IO, String, Unit] =
      for {
        _ <- Episode.Next[IO, String](_ => true)
        _ <- Episode.Next(predicate)
      } yield ()

    def episode(ref: Ref[IO, Int]): Episode[IO, String, Unit] =
      Episode.Tolerate(original, None, _ => ref.update(_ + 1))

    val input = Stream("", "")
    val counter = Stream.eval(Ref[IO].of(0)).flatMap { ref =>
      input.through(episode(ref).matching).drain ++ Stream.eval(ref.get)
    }

    assert(counter.value() == 1)
  }

  test("Episode.Next#tolerateN skips up to N elements if they don't match") {
    val n = 5
    val episode: Episode[IO, String, String] =
      for {
        _ <- Episode.Next[IO, String](_ => true)
        s <- Episode.Tolerate[IO, String, String](
          Episode.Next(predicate),
          Some(n),
          _ => IO.unit
        )
      } yield s

    val input = Stream("any") ++ Stream("").repeatN(5) ++ Stream(s"2.$expected")

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

  test("Episode doesn't throw exception during the interpretation") {
    case class Error(s: String) extends Throwable

    val input: Stream[IO, Int] =
      Stream(Right(1), Left(Error("test")), Right(2), Right(3)).evalMap {
        case Right(i)    => IO.pure(i)
        case Left(error) => IO.raiseError(error)
      }

    val episode: Episode[IO, Int, Unit] =
      for {
        _ <- Episode.Next[IO, Int](_ => true)
        _ <- Episode.Next[IO, Int](_ => true)
      } yield ()

    input.through(episode.attempt.matching).run()
  }
}
