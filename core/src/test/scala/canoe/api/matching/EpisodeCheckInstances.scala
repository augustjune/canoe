package canoe.api.matching

import canoe.TestIO._
import cats.Eq
import cats.effect.IO
import cats.syntax.applicativeError._
import fs2.Stream
import org.scalacheck.{Arbitrary, Gen}

object EpisodeCheckInstances {

  implicit def eqEpisode[I: Arbitrary, O]: Eq[Episode[IO, I, O]] = {
    val sampleInput: List[I] = Gen.listOf(Arbitrary.arbitrary[I]).sample.get

    def result(ep: Episode[IO, I, O]): List[Either[Throwable, O]] =
      Stream.emits(sampleInput).through(ep.attempt.matching).toList()

    (x: Episode[IO, I, O], y: Episode[IO, I, O]) =>
      result(x) == result(y)
  }

  implicit val eqThrowable: Eq[Throwable] =
    (x: Throwable, y: Throwable) => (x ne null) == (y ne null)

  implicit def arbEpisode[F[_], I, O: Arbitrary]: Arbitrary[Episode[F, I, O]] =
    Arbitrary(
      Gen.oneOf(
        Arbitrary.arbitrary[O].map(o => Episode.Pure[F, O](o)),
        for {
          b <- Arbitrary.arbBool.arbitrary
          o <- Arbitrary.arbitrary[O]
        } yield Episode.First[F, I](_ => b).map(_ => o),
        for {
          b <- Arbitrary.arbBool.arbitrary
          o <- Arbitrary.arbitrary[O]
        } yield Episode.Next[F, I](_ => b).map(_ => o)
      )
    )

}
