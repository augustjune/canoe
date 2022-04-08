package canoe

import cats.effect.IO
import fs2.Stream

import scala.concurrent.ExecutionContext
import cats.effect.Temporal

object TestIO {

  implicit class IOStreamOps[A](stream: Stream[IO, A]) {
    def toList(): List[A] = stream.compile.toList.unsafeRunSync()

    def value(): A = toList().head

    def count(): Int = toList().size

    def run(): Unit = stream.compile.drain.unsafeRunSync()
  }

  implicit val globalContext: ContextShift[IO] = IO.contextShift(ExecutionContext.global)
  implicit val globalTimer: Temporal[IO] = IO.timer(ExecutionContext.global)
}
