package com.canoe.telegram.scenarios

import cats.Id
import org.scalacheck.{Arbitrary, Gen}

object ScenarioTestInstances {
  implicit def scenarioArbitrary[A: Arbitrary]: Arbitrary[Scenario[Id, A]] = ???
}
