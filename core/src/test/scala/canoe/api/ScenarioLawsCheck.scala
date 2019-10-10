package canoe.api

import canoe.api.ScenarioCheckInstances._
import cats.Monad
import cats.effect.IO
import cats.implicits._
import cats.laws.discipline._
import org.scalatest.funsuite.AnyFunSuite
import org.typelevel.discipline.scalatest.Discipline

class ScenarioLawsCheck extends AnyFunSuite with Discipline {

  // By re-declaring implicit Monad instance here, we ensure that it will be used during monad tests,
  // instead of MonadError instance
  implicit def monadInstance[F[_], I]: Monad[Scenario[F, *]] = Scenario.monadInstance

  checkAll("Monad[Scenario[IO, *]]",
    MonadTests[Scenario[IO, *]].monad[Int, String, Int])

  checkAll("MonadError[Scenario[IO, *], Throwable]",
    MonadErrorTests[Scenario[IO, *], Throwable].monadError[String, Int, String])
}
