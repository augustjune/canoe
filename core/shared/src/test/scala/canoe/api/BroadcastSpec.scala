package canoe.api

import canoe.TestIO._
import cats.effect.IO
import fs2.{Pipe, Stream}
import org.scalatest.freespec.AnyFreeSpec
import cats.effect.concurrent.Ref
import scala.concurrent.duration._

class BroadcastSpec extends AnyFreeSpec {
  def broadcast[A]: Stream[IO, Broadcast[IO, A]] = Stream.eval(Broadcast[IO, A])

  def recordPulled[A](b: Broadcast[IO, A], duration: FiniteDuration): Pipe[IO, A, List[A]] =
    input =>
      Stream.eval(Ref[IO].of(List.empty[A])).flatMap { ref =>
        input
          .evalTap(i => ref.update(i :: _))
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
          val pop = Stream.sleep_(0.05.second) ++ input.through(b.publish)
          val sub = b.subscribe(1).take(input.size())
          sub.concurrently(pop)
        }

        assert(res.toList() == input.toList)
      }

      "is deregistered after it is done pulling" in {
        val pulled = broadcast[Int].flatMap { b =>
          val pop = Stream.sleep_(0.1.second) ++ input.through(recordPulled(b, 1.second))
          val consumer = b.subscribe(1).metered(0.1.second).take(5)
          pop.concurrently(consumer)
        }

        assert(pulled.value() == input.toList.reverse)
      }
    }

    "pulls from publisher" - {
      "one element before it's blocked by the subscriber" in {
        val pulled = broadcast[Int].flatMap { b =>
          val pop = Stream.sleep_(0.05.second) ++ input.through(recordPulled(b, 0.2.second))
          val consumer = b.subscribe(0).evalMap(_ => IO.never)
          pop.concurrently(consumer)
        }

        assert(pulled.value() == input.head.toList)
      }

      "maxQueued + 2 elements for non-empty blocking consumer" in {
        val maxQueued = 3
        val pulled = broadcast[Int].flatMap { b =>
          val pop = Stream.sleep_(0.05.second) ++ input.through(recordPulled(b, 0.2.second))
          val consumer = b.subscribe(maxQueued).evalMap(_ => IO.never)
          pop.concurrently(consumer)
        }
        assert(pulled.value() == input.take(maxQueued + 2).toList.reverse)
      }

      "all elements" - {
        "for non-blocking consumer" in {
          val pulled = broadcast[Int].flatMap { b =>
            val pop = input.through(recordPulled(b, 0.2.second))
            val consumer = b.subscribe(1)
            pop.concurrently(consumer)
          }
          assert(pulled.value() == input.toList.reverse)
        }

        "for no consumer" in {
          val pulled = broadcast[Int].flatMap { b =>
            input.through(recordPulled(b, 0.2.second))
          }
          assert(pulled.value() == input.toList.reverse)
        }
      }
    }
  }
}
