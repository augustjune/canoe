package canoe

import cats.effect.IO
import cats.effect.testing.scalatest.AsyncIOSpec
import fs2.Stream
import org.scalatest.AsyncTestSuite

trait IOSpec extends AsyncIOSpec { asyncTestSuite: AsyncTestSuite =>

  implicit class IOStreamOps[A](stream: Stream[IO, A]) {
    def toList(): List[A] = stream.compile.toList.unsafeRunSync()

    def value(): A = toList().head

    def count(): Int = toList().size

    def run(): Unit = stream.compile.drain.unsafeRunAndForget()
  }
}
