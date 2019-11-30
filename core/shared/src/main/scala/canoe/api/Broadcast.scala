package canoe.api

import cats.syntax.all._
import cats.instances.list._
import cats.effect.{Concurrent}
import cats.effect.concurrent.Ref
import fs2.{Stream, Pipe}
import fs2.concurrent.{Queue, Topic}

private[api] class Broadcast[F[_], A](subs: Ref[F, List[Queue[F, A]]])(implicit C: Concurrent[F])
    extends Topic[F, A] {

  def publish: Pipe[F, A, Unit] =
    _.evalMap(publish1)

  def publish1(a: A): F[Unit] =
    subs.get.flatMap { subscribers => 
      subscribers.traverse(_.enqueue1(a)).void
    }

  def subscribe(maxQueued: Int): Stream[F, A] =
    emptyQueue(maxQueued).evalTap(q => subs.update(q :: _)).flatMap(_.dequeue)

  private def emptyQueue(maxQueued: Int): Stream[F, Queue[F, A]] = {
    Stream.bracket(Queue.bounded[F, A](maxQueued))(
      queue => subs.update(_.filter(_ ne queue))
    )
  }

  def subscribeSize(maxQueued: Int): Stream[F,(A, Int)] = 
    subscribe(maxQueued).zip(subscribers)

  def subscribers: Stream[F,Int] = Stream.repeatEval(subs.get).map(_.size)
}

object Broadcast {
  def apply[F[_], A](implicit C: Concurrent[F]): F[Broadcast[F, A]] =
    Ref.of[F, List[Queue[F, A]]](List.empty).map(new Broadcast(_))
}