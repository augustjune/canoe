package canoe.api

import canoe.IOSpec
import canoe.models.PrivateChat
import canoe.models.messages.{TelegramMessage, TextMessage}
import cats.Eq
import cats.effect.IO
import fs2.Stream
import org.scalacheck.{Arbitrary, Gen}

trait ScenarioCheckInstances { self: IOSpec =>
  // Basically the same instances as in EpisodeCheckInstances

  private def message(s: String): TextMessage =
    TextMessage(-1, PrivateChat(-1, None, None, None), -1, s)

  implicit def arbMessage: Arbitrary[TelegramMessage] =
    Arbitrary(Arbitrary.arbString.arbitrary.map(message))

  implicit def eqScenario[A]: Eq[Scenario[IO, A]] = {

    val inputGen =
      Gen.listOf(
        Gen.frequency(
          1 -> Arbitrary.arbException.arbitrary.map(Left(_)),
          10 -> Arbitrary.arbitrary[TelegramMessage].map(Right(_))
        )
      )

    val input = Stream.emits(inputGen.sample.get).evalMap {
      case Right(i) => IO.pure(i)
      case Left(e)  => IO.raiseError(e)
    }

    def result(sc: Scenario[IO, A]): List[Either[Throwable, A]] =
//      input.through(sc.attempt.pipe).toList()
      List()

    (x: Scenario[IO, A], y: Scenario[IO, A]) =>
      result(x) == result(y)
  }

  implicit val eqThrowable: Eq[Throwable] = Eq.fromUniversalEquals[Throwable]

  implicit def arbScenario[F[_], A: Arbitrary]: Arbitrary[Scenario[F, A]] =
    Arbitrary(
      Gen.oneOf(
        Arbitrary.arbitrary[A].map(a => Scenario.pure[F](a)),
        for {
          b <- Arbitrary.arbBool.arbitrary
          a <- Arbitrary.arbitrary[A]
        } yield Scenario.expect[F, A] { case _ if b => a }
      )
    )

}
