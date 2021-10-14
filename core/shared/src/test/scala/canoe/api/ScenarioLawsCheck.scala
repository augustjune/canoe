package canoe.api

import canoe.IOSpec
import cats.effect.IO
import cats.laws.discipline._
import org.scalatest.AsyncTestSuite
import org.scalatest.funsuite.AnyFunSuite
import org.typelevel.discipline.scalatest.Discipline

class ScenarioLawsCheck
    extends AnyFunSuite
    with Discipline
    with AsyncTestSuite
    with IOSpec
    with ScenarioCheckInstances {

  checkAll("MonadError[Scenario[IO, *], Throwable]",
           MonadErrorTests[Scenario[IO, *], Throwable].monadError[String, Int, String]
  )
}
