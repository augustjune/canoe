package canoe.api

import cats.syntax.all._
import cats.effect.{Concurrent}
import fs2.{Pipe, Stream}
import fs2.concurrent.{Queue, Topic}
import cats.effect.Ref

/**
  * Custom, more efficient implementation of fs2.concurrent.Topic.
  * Each subscriber has own queue where each incoming elements is stored.
  *
  * Unlike stanard Topic impemenatation, doesn't contain initial value,
  * thus each subscriber sees only elements that were published after
  * the subscription was done.
  */
private[api] class Broadcast[F[_], A](subs: Ref[F, List[Queue[F, A]]])(implicit C: Concurrent[F]) extends Topic[F, A] {
  def publish: Pipe[F, A, Unit] =
    _.evalMap(publish1)

  def publish1(a: A): F[Unit] =
    subs.get.flatMap(_.traverse_(_.enqueue1(a)))

  def subscribe(maxQueued: Int): Stream[F, A] =
    subscription(maxQueued).evalTap(q => subs.update(q :: _)).flatMap(_.dequeue)

  private def subscription(maxQueued: Int): Stream[F, Queue[F, A]] =
    Stream.bracket(Queue.bounded[F, A](maxQueued)) { q =>
      subs.update(_.filter(_ ne q)) *> q.tryDequeue1.void
    }

  def subscribeSize(maxQueued: Int): Stream[F, (A, Int)] =
    subscribe(maxQueued).zip(subscribers)

  def subscribers: Stream[F, Int] =
    Stream.repeatEval(subs.get).map(_.size)
}

object Broadcast {
  private [api] def apply[F[_], A](implicit C: Concurrent[F]): F[Broadcast[F, A]] =
    Ref.of[F, List[Queue[F, A]]](List.empty).map(new Broadcast(_))
}
