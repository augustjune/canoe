package com.canoe.telegram.scenarios

import cats.effect.concurrent.Ref
import cats.effect.{Concurrent, ExitCode, IO, IOApp, Sync}
import cats.syntax.functor._
import fs2.concurrent.{Broadcast, Queue, Topic}
import fs2.{Pipe, Pull, Stream}

import scala.io.StdIn
import scala.concurrent.duration._
import scala.util.Random

object StreamTest extends IOApp {

  // results with single element (head -> tail)
  def composeSingle(s: Stream[IO, String]): Stream[IO, (String, Stream[IO, String])] = {
    s.map(m => m -> s.dropThrough(_ != m).take(3))
  }

  def run(args: List[String]): IO[ExitCode] = broadcastGroupExample

  def readConsole: Stream[IO, String] = Stream.repeatEval(IO(StdIn.readLine()))

  def broadcastExample: IO[ExitCode] =
    readConsole.evalTap(log[IO]("initial")).broadcast.zipWithIndex.map {
      case (worker, i) =>
        worker.evalMap { o => IO(println(s"$i: $o")) }
    }.take(3).parJoinUnbounded.compile.drain.as(ExitCode.Success)

  // returns first element which matches predicate along with each element after that
  def receiveClone(p: String => Boolean)(s: Stream[IO, String]): Stream[IO, (String, Stream[IO, String])] =
    s.filter(p).map(_ -> s.take(3))


  val alphabet: List[String] = List("A", "B", "C", "D", "E", "F", "G")

  def randomNums(n: Int): Stream[IO, Int] = Stream.iterate(0)(_ => Random.nextInt(n)).covaryId[IO]

  def example: IO[ExitCode] =
    Stream.fromIterator[IO, String](alphabet.iterator)
      .evalTap(log[IO]("consumed"))
      .through(gPipe[IO])
      .broadcast.zipWithIndex.map { case (s, i) => s.drop(i).take(1).evalTap { case (h, t) => log[IO](s"broadcast$i")(s"$h -> $t") } }
      .map { s => s.evalMap { case (h, rest) => rest.compile.toList.map(h -> _) } } //s.map case (h, rest) => rest.compile.toList.map(h -> _) }
      .parJoinUnbounded
      .evalMap { case (h, rest) => IO(println(s"$h: $rest")) }
      .compile.drain.as(ExitCode.Success)

  def gPipe[F[_] : Sync](m: Stream[F, String]): Stream[F, (String, Stream[F, String])] =
    m.map(_ -> m.take(3))

  def broadcastShift: IO[ExitCode] =
    Stream.fromIterator[IO, String](alphabet.iterator)
      .evalTap(log[IO]("consumed"))
      .broadcast
      .zipWithIndex
      .map { case (s, i) => s.drop(i).evalTap(log[IO](i.toString)).metered(1.second) }
      .take(5)
      .parJoinUnbounded
      .compile.drain.as(ExitCode.Success)

  def broadcastGroupExample: IO[ExitCode] = {
    val smallInput = Stream.fromIterator[IO, (Int, String)](Iterator(
      1 -> "Hello",
      2 -> "Hi girl",
      1 -> "How are you doing?",
      3 -> "Wassup?",
      2 -> "Let's go to the mall?",
      3 -> "yo, bro",
      1 -> "This evening is nice",
      1 -> "Is the office as good as they tell?",
      3 -> "yo"
    )).evalTap(log[IO]("read")).metered(1.second)

    val input =
      randomNums(10)
        .evalTap(log[IO]("read")).metered(1.second)
        .map(i => i -> i.toString )


    broadcastGroup(input)
      .zipWithIndex.map { case (s, i) => s.evalTap(log[IO](s"stream $i")) }
      .take(2)
      .parJoinUnbounded
      .compile.drain.as(ExitCode.Success)
  }

  def broadcastGroup[F[_] : Concurrent](input: Stream[F, (Int, String)]): Stream[F, Stream[F, (Int, String)]] = {

    def filterByFirst(s: Stream[F, (Int, String)]): Stream[F, (Int, String)] = {
      def go(in: Stream[F, (Int, String)]): Pull[F, (Int, String), Unit] =
        in.pull.peek1.flatMap {
          case Some(((id, _), rest)) => rest.filter { case (i, _) => i == id }.pull.echo
          case None => Pull.done
        }

      go(s).stream
    }

    /**
      * Returns stream of one element, where the element might be
      * Some(stream) - for the stream of integers which is not in the set yet
      * None - for the stream of integers, which is already in the set
      */
    def validateOption(ref: Ref[F, Set[Int]])(stream: Stream[F, (Int, String)]): Stream[F, Option[Stream[F, (Int, String)]]] = {

      def go(in: Stream[F, (Int, String)]): Pull[F, Option[Stream[F, (Int, String)]], Unit] =
        in.pull.peek1.flatMap {
          case Some(((i, _), rest)) =>
            Pull.eval(ref.modify { ids =>
              if (ids(i)) ids -> false
              else (ids + i) -> true
            }).flatMap {
              case true => Pull.output1(Some(rest))
              case false => Pull.output1(None)
            }

          case None => Pull.output1(None)
        }

      go(stream).stream
    }

    Stream.eval(Ref.of[F, Set[Int]](Set.empty[Int])).flatMap { idsRef =>
      input.broadcast
        .zipWithIndex.map { case (s, i) => s.drop(i) }
        .map(filterByFirst)
        .flatMap(validateOption(idsRef))
        .unNone
    }
  }

  def log[F[_] : Sync](id: String)(a: Any): F[Unit] = Sync[F].delay(println(s"$id: $a"))
}
