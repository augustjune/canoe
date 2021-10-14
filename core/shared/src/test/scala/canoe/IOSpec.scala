package canoe

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import fs2.Stream
import org.scalatest.AsyncTestSuite

trait IOSpec extends AsyncIOSpec { asyncTestSuite: AsyncTestSuite =>

  implicit class IOStreamOps[A](stream: Stream[IO, A]) {
    def value(stream: Stream[IO, A]) = new CompiledStream(stream)
  }

  class CompiledStream[A](stream: Stream[IO, A]) {
    def list: IO[List[A]] = stream.compile.toList

    def head: IO[A] = stream.head.compile.lastOrError

    def count: IO[Long] = stream.compile.count

    def drain: IO[Unit] = stream.compile.drain
  }
}
