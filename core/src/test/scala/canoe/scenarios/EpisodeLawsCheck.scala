package canoe.scenarios

import canoe.scenarios.EpisodeCheckInstances._
import cats.effect.IO
import cats.implicits._
import cats.laws.discipline.MonadTests
import org.scalatest.funsuite.AnyFunSuite
import org.typelevel.discipline.scalatest.Discipline

class EpisodeLawsCheck extends AnyFunSuite with Discipline {
  checkAll("Episode.MonadLaws", MonadTests[Episode[IO, Int, *]].monad[Int, Int, Int])
}
