package canoe.api.matching

import canoe.IOSpec
import cats.effect.IO
import cats.laws.discipline._
import org.scalatest.AsyncTestSuite
import org.scalatest.funsuite.AnyFunSuite
import org.typelevel.discipline.scalatest.Discipline

class EpisodeLawsCheck
    extends AnyFunSuite
    with Discipline
    with AsyncTestSuite
    with IOSpec
    with EpisodeCheckInstances {

  checkAll("MonadError[Episode[IO, Int, *], Throwable]",
           MonadErrorTests[Episode[IO, Int, *], Throwable].monadError[Int, Int, Int]
  )
}
