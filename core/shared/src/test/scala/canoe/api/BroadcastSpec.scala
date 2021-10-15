package canoe.api

import canoe.IOSpec
import cats.effect.IO
import fs2.{Pipe, Stream}
import cats.effect.Ref
import org.scalatest.freespec.AsyncFreeSpec

import scala.concurrent.duration._

class BroadcastSpec extends AsyncFreeSpec with IOSpec {
  def broadcast[A]: Stream[IO, Broadcast[IO, A]] = Stream.eval(Broadcast[IO, A])

  def recordPulled[A](b: Broadcast[IO, A], duration: FiniteDuration): Pipe[IO, A, List[A]] =
    input =>
      Stream.eval(Ref[IO].of(List.empty[A])).flatMap { ref =>
        input
          .evalTap(i => ref.update(_ :+ i))
          .through(b.publish)
          .drain
          .interruptAfter(duration)
          .append(Stream.eval(ref.get))
      }

  "Broadcast" - {
    val input = Stream.range(1, 100)

    "subscriber" - {
      "sees all elements after subscription" in {
        val res = broadcast[Int].flatMap { b =>
          val pop = Stream.sleep_[IO](0.05.second) ++ input.through(b.publish)
          val sub = b.subscribe(1).take(input.compile.count)
          sub.concurrently(pop)
        }

        res.compile.toList.flatMap { list =>
          IO(assert(list == input.toList))
        }
      }

      "is deregistered after it is done pulling" in {
        val pulled = broadcast[Int].flatMap { b =>
          val pop = Stream.sleep_[IO](0.1.second) ++ input.through(recordPulled(b, 1.second))
          val consumer = b.subscribe(1).metered(0.1.second).take(5)
          pop.concurrently(consumer)
        }

        pulled.head.compile.lastOrError.flatMap { list =>
          IO(assert(list == input.toList))
        }
      }
    }

    "pulls from publisher" - {
      "maxQueued + 2 elements for blocking consumer" in {
        val maxQueued = 3
        val pulled = broadcast[Int].flatMap { b =>
          val pop = Stream.sleep_[IO](0.05.second) ++ input.through(recordPulled(b, 0.2.second))
          val consumer = b.subscribe(maxQueued).evalMap(_ => IO.never)
          pop.concurrently(consumer)
        }
        pulled.head.compile.lastOrError.flatMap { list =>
          IO(assert(list == input.take(maxQueued + 2).toList))
        }
      }

      "all elements" - {
        "for non-blocking consumer" in {
          val pulled = broadcast[Int].flatMap { b =>
            val pop = input.through(recordPulled(b, 0.2.second))
            val consumer = b.subscribe(1)
            pop.concurrently(consumer)
          }

          pulled.head.compile.lastOrError.flatMap { list =>
            IO(assert(list == input.toList))
          }

        }

        "for no consumer" in {
          val pulled = broadcast[Int].flatMap { b =>
            input.through(recordPulled(b, 0.2.second))
          }
          pulled.head.compile.lastOrError.flatMap { list =>
            IO(assert(list == input.toList))
          }
        }
      }
    }
  }
}
