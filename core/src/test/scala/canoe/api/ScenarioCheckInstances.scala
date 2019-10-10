package canoe.api

import canoe.TestIO._
import canoe.models.PrivateChat
import canoe.models.messages.{TelegramMessage, TextMessage}
import cats.Eq
import cats.effect.IO
import fs2.Stream
import org.scalacheck.{Arbitrary, Gen}

object ScenarioCheckInstances {
  // Basically the same instances as in EpisodeCheckInstances

  private def message(s: String): TextMessage =
    TextMessage(-1, PrivateChat(-1, None, None, None), -1, s)

  implicit def arbMessage: Arbitrary[TelegramMessage] =
    Arbitrary(Arbitrary.arbString.arbitrary.map(message))

  implicit def eqScenario[A]: Eq[Scenario[IO, A]] = {

    val sampleInput: List[TelegramMessage] =
      Gen.listOf(Arbitrary.arbitrary[TelegramMessage]).sample.get

    def result(sc: Scenario[IO, A]): List[Either[Throwable, A]] =
      Stream.emits(sampleInput).through(sc.attempt.pipe).toList()

    (x: Scenario[IO, A], y: Scenario[IO, A]) =>
      result(x) == result(y)
  }

  implicit val eqThrowable: Eq[Throwable] =
    (x: Throwable, y: Throwable) => (x ne null) == (y ne null)

  implicit def arbScenario[F[_], A: Arbitrary]: Arbitrary[Scenario[F, A]] =
    Arbitrary(
      Gen.oneOf(
        Arbitrary.arbitrary[A].map(a => Scenario.pure[F, A](a)),
        for {
          b <- Arbitrary.arbBool.arbitrary
          a <- Arbitrary.arbitrary[A]
        } yield Scenario.start[F, A] { case _ if b => a },
        for {
          b <- Arbitrary.arbBool.arbitrary
          a <- Arbitrary.arbitrary[A]
        } yield Scenario.next[F, A] { case _ if b => a }
      )
    )

}
