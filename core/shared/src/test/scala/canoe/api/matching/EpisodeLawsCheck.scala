package canoe.api.matching

import canoe.api.matching.EpisodeCheckInstances._
import cats.effect.IO
import cats.implicits._
import cats.laws.discipline._
import org.scalatest.funsuite.AnyFunSuite
import org.typelevel.discipline.scalatest.Discipline

class EpisodeLawsCheck extends AnyFunSuite with Discipline {

  checkAll("MonadError[Episode[IO, Int, *], Throwable]",
    MonadErrorTests[Episode[IO, Int, *], Throwable].monadError[Int, Int, Int])
}
