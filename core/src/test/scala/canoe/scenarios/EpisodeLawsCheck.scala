package canoe.scenarios

import canoe.scenarios.EpisodeCheckInstances._
import cats.Id
import cats.implicits._
import cats.laws.discipline.MonadTests
import org.scalatest.funsuite.AnyFunSuite
import org.typelevel.discipline.scalatest.Discipline

class EpisodeLawsCheck extends AnyFunSuite with Discipline {
  checkAll("Episode.MonadLaws", MonadTests[Episode[Id, Int, *]].monad[Int, Int, Int])
}
