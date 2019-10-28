package canoe.api.matching

import canoe.api.matching.Episode._
import cats.instances.list._
import cats.syntax.all._
import cats.{ApplicativeError, Monad, MonadError, StackSafeMonad, ~>}
import fs2.{Pipe, Pull, Stream}

/**
  * Type which represents a description of sequence of elements.
  *
  * Such description can be applied to the actual sequence/stream of elements
  * to find the subsequences which match the description.
  *
  * For each partially matched subsequence (such that at least first element was matched)
  * one of 4 possible results is yielded:
  *  - Matched(o)   - when the whole episode was matched as a subsequence of input sequence.
  *                   Contains the result value of type `O`.
  *  - Missed(i)    - when the episode was started (partially matched) but was not fully matched.
  *                   Contains the first element of type `I` which didn't match the description.
  *  - Cancelled(i) - when the episode was cancelled by specific input.
  *                   Contains the input element of type `I` which caused cancellation.
  *  - Failed(e)    - when evaluated program resulted into failure.
  *                   Contains the error (Throwable) which was raised during the evaluation.
  *
  * `Episode` forms a monad in `O` with `pure` and `flatMap`.
  *
  * @tparam F Effect type
  * @tparam I Input elements type
  * @tparam O Result value type
  */
private[api] sealed trait Episode[F[_], -I, +O] {

  /**
    * Pipe which produces value `O` for each subsequence of `I` elements
    * matching this description, evaluated in `F` effect.
    * Fails on the first unhandled error result.
    */
  def matching: Pipe[F, I, O] =
    find(this, _, Nil, Nil).collect {
      case (Matched(o), _) => o
      case (Failed(e), _)  => throw e
    }

  def flatMap[I2 <: I, O2](fn: O => Episode[F, I2, O2]): Episode[F, I2, O2] =
    Bind(this, fn)

  def map[O2](fn: O => O2): Episode[F, I, O2] = flatMap(fn.andThen(Pure(_)))

  def mapK[G[_]](fn: F ~> G): Episode[G, I, O] = translate(this, fn)
}

object Episode {

  private[api] final case class Pure[F[_], A](a: A) extends Episode[F, Any, A]

  private[api] final case class Eval[F[_], A](fa: F[A]) extends Episode[F, Any, A]

  private[api] final case class Next[F[_], A](p: A => Boolean) extends Episode[F, A, A]

  private[api] final case class First[F[_], A](p: A => Boolean) extends Episode[F, A, A]

  private[api] final case class Protected[F[_], I, O1, O2 >: O1](episode: Episode[F, I, O1],
                                                                 onError: Throwable => Episode[F, I, O2])
      extends Episode[F, I, O2]

  private[api] final case class Bind[F[_], I, O1, O2](episode: Episode[F, I, O1], fn: O1 => Episode[F, I, O2])
      extends Episode[F, I, O2]

  private[api] final case class Cancellable[F[_], I, O](episode: Episode[F, I, O],
                                                        cancelOn: I => Boolean,
                                                        finalizer: Option[I => F[Unit]])
      extends Episode[F, I, O]

  private[api] final case class Tolerate[F[_], I, O](episode: Episode[F, I, O], limit: Option[Int], fn: I => F[Unit])
      extends Episode[F, I, O]

  private[api] implicit def monadErrorInstance[F[_]: ApplicativeError[*[_], Throwable], I]
    : MonadError[Episode[F, I, *], Throwable] =
    new MonadError[Episode[F, I, *], Throwable] with StackSafeMonad[Episode[F, I, *]] {

      def pure[A](a: A): Episode[F, I, A] = Pure(a)

      def flatMap[A, B](fa: Episode[F, I, A])(f: A => Episode[F, I, B]): Episode[F, I, B] =
        fa.flatMap(f)

      def handleErrorWith[A](fa: Episode[F, I, A])(f: Throwable => Episode[F, I, A]): Episode[F, I, A] =
        Protected(fa, f)

      def raiseError[A](e: Throwable): Episode[F, I, A] = Eval(e.raiseError[F, A])
    }

  private[api] implicit def monadInstance[F[_], I]: Monad[Episode[F, I, *]] =
    new StackSafeMonad[Episode[F, I, *]] {

      def pure[A](a: A): Episode[F, I, A] = Pure(a)

      def flatMap[A, B](episode: Episode[F, I, A])(fn: A => Episode[F, I, B]): Episode[F, I, B] =
        episode.flatMap(fn)
    }

  private sealed trait Result[+I, +O] {
    def map[O1](f: O => O1): Result[I, O1] = this match {
      case Matched(o)          => Matched(f(o))
      case same @ Missed(_)    => same
      case same @ Cancelled(_) => same
      case same @ Failed(_)    => same
    }
  }

  private final case class Matched[O](o: O) extends Result[Nothing, O]
  private final case class Missed[I](elem: I) extends Result[I, Nothing]
  private final case class Cancelled[I](elem: I) extends Result[I, Nothing]
  private final case class Failed(e: Throwable) extends Result[Nothing, Nothing]

  private sealed trait BindF {
    def isHandler: Boolean
  }

  private final case class ErrorHandle[F[_], I](fn: Throwable => Episode[F, I, Any]) extends BindF {
    def isHandler: Boolean = true
  }
  private final case class FlatMap[F[_], I](fn: Any => Episode[F, I, Any]) extends BindF {
    def isHandler: Boolean = false
  }

  private def find[F[_], I, O](
    episode: Episode[F, I, O],
    input: Stream[F, I],
    cancelTokens: List[(I => Boolean, Option[I => F[Unit]])],
    bindStack: List[BindF]
  ): Stream[F, (Result[I, O], Stream[F, I])] =
    episode match {
      case Pure(a) =>
        bindStack.dropWhile(_.isHandler) match {
          case FlatMap(fn) :: stack =>
            find(fn(a).asInstanceOf[Episode[F, I, O]], input, cancelTokens, stack)

          case empty =>
            Stream(a).covary[F].map(o => Matched(o) -> input)
        }

      case First(p: (I => Boolean)) =>
        def go(in: Stream[F, I]): Pull[F, (Result[I, O], Stream[F, I]), Unit] =
          in.dropWhile(!p(_)).pull.uncons1.flatMap {
            case Some((m, rest)) =>
              Pull.output1(Matched(m.asInstanceOf[O]) -> rest) >> go(rest)

            case None => Pull.done
          }

        bindStack.dropWhile(_.isHandler) match {
          case FlatMap(fn) :: stack =>
            go(input).stream.flatMap {
              case (Matched(o), rest) => find(fn(o).asInstanceOf[Episode[F, I, O]], rest, cancelTokens, stack)
              case other              => Stream(other)
            }
          case empty => go(input).stream
        }

      case Next(p: (I => Boolean)) =>
        def go(in: Stream[F, I]): Pull[F, (Result[I, O], Stream[F, I]), Unit] =
          in.pull.uncons1.flatMap {
            case Some((m, rest)) =>
              cancelTokens.collect { case (p, f) if p(m) => f } match {
                case Nil =>
                  if (p(m)) Pull.output1(Matched(m.asInstanceOf[O]) -> rest)
                  else Pull.output1(Missed(m) -> rest)

                case nonEmpty =>
                  nonEmpty
                    .collect { case Some(f) => f }
                    .traverse(f => Pull.eval(f(m))) >>
                    Pull.output1(Cancelled(m) -> rest)
              }
            case None => Pull.done
          }

        bindStack.dropWhile(_.isHandler) match {
          case FlatMap(fn) :: stack =>
            go(input).stream.flatMap {
              case (Matched(o), rest) => find(fn(o).asInstanceOf[Episode[F, I, O]], rest, cancelTokens, stack)
              case other              => Stream(other)
            }
          case empty => go(input).stream
        }

      case Eval(fa) =>
        Stream.eval(fa).attempt.flatMap {
          case Left(e) =>
            bindStack.dropWhile(!_.isHandler) match {
              case ErrorHandle(fn) :: stack =>
                find(fn(e).asInstanceOf[Episode[F, I, O]], input, cancelTokens, stack)

              case empty =>
                Stream(Failed(e) -> input)
            }

          case Right(a) =>
            bindStack.dropWhile(_.isHandler) match {
              case FlatMap(fn) :: stack =>
                find(fn(a).asInstanceOf[Episode[F, I, O]], input, cancelTokens, stack)

              case empty =>
                Stream(Matched(a) -> input)
            }
        }

      case Protected(episode, onError) =>
        find(episode, input, cancelTokens, ErrorHandle(onError) :: bindStack)

      case Cancellable(episode, p, f) =>
        find(episode, input, (p, f) :: cancelTokens, bindStack)

      case Tolerate(episode, limit, fn) =>
        limit match {
          case Some(n) if n <= 0 =>
            find(episode, input, cancelTokens, bindStack)

          case _ =>
            find(episode, input, cancelTokens, bindStack).flatMap {
              case (Missed(m), rest) =>
                Stream.eval(fn(m)) >> find(
                  Tolerate(episode, limit.map(_ - 1), fn),
                  rest,
                  cancelTokens,
                  bindStack
                )

              case res => Stream(res)
            }
        }

      case Bind(prev, fn) =>
        find[F, I, O](prev.asInstanceOf[Episode[F, I, O]],
                      input,
                      cancelTokens,
                      FlatMap(fn.asInstanceOf[Any => Episode[F, I, Any]]) :: bindStack)
    }

  private def translate[F[_], G[_], I, O](episode: Episode[F, I, O], f: F ~> G): Episode[G, I, O] =
    episode match {
      case Pure(a)                    => Pure(a)
      case Eval(fa)                   => Eval(f(fa))
      case Next(p)                    => Next(p).asInstanceOf[Episode[G, I, O]]
      case First(p)                   => First(p).asInstanceOf[Episode[G, I, O]]
      case Protected(ep, onError)     => Protected(ep.mapK(f), onError.andThen(_.mapK(f)))
      case Bind(ep, fn)               => Bind(ep.mapK(f), fn.andThen(_.mapK(f)))
      case Tolerate(ep, limit, fn)    => Tolerate(ep.mapK(f), limit, fn.andThen(f(_)))
      case Cancellable(ep, canc, fin) => Cancellable(ep.mapK(f), canc, fin.map(_.andThen(f(_))))
    }
}
