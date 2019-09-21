package canoe.scenarios

import canoe.TestIO._
import cats.effect.IO
import cats.implicits._
import cats.{Eq, Id}
import fs2.Stream
import org.scalacheck.{Arbitrary, Gen}

object EpisodeCheckInstances {

  implicit def eqEpisode[I: Arbitrary, O]: Eq[Episode[Id, I, O]] = {
    val sampleInput: List[I] = Gen.listOf(Arbitrary.arbitrary[I]).sample.get

    def result(ep: Episode[Id, I, O]): List[O] =
      Stream.emits(sampleInput).through(ep.pipe).covaryId[IO].toList()

    (x: Episode[Id, I, O], y: Episode[Id, I, O]) =>
      result(x) == result(y)
  }


  implicit def arbEpisode[F[_], I, O: Arbitrary]: Arbitrary[Episode[F, I, O]] =
    Arbitrary(
      Gen.oneOf(
        Arbitrary.arbitrary[O].map(o => Episode.pure[F, I, O](o)),
        for {
          b <- Arbitrary.arbBool.arbitrary
          o <- Arbitrary.arbitrary[O]
        } yield Episode.first[F, I](_ => b).map(_ => o),
        for {
          b <- Arbitrary.arbBool.arbitrary
          o <- Arbitrary.arbitrary[O]
        } yield Episode.next[F, I](_ => b).map(_ => o)
      )
    )

}
