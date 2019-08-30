import cats.syntax.functor._
import cats.effect.{ExitCode, IO, IOApp}
import fs2.Stream

object ExampleRun extends IOApp {
  def run(args: List[String]): IO[ExitCode] =
    Stream.range(1, 3).covary[IO].evalTap(i => IO(println(i))).compile.drain.as(ExitCode.Success)
}
