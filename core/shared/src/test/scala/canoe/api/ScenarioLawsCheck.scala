package canoe.api

import canoe.api.ScenarioCheckInstances._
import cats.effect.IO
import cats.implicits._
import cats.laws.discipline._
import org.scalatest.funsuite.AnyFunSuite
import org.typelevel.discipline.scalatest.Discipline

class ScenarioLawsCheck extends AnyFunSuite with Discipline {

  checkAll("MonadError[Scenario[IO, *], Throwable]",
    MonadErrorTests[Scenario[IO, *], Throwable].monadError[String, Int, String])
}
