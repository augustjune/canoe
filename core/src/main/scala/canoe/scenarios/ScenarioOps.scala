package canoe.scenarios

import canoe.api.Scenario
import canoe.syntax._

final class ScenarioOps[F[_], A](private val scenario: Scenario[F, A]) extends AnyVal {
  def cancelOnText(message: String): Scenario[F, A] =
    cancelWhen(text.when(_ == message))

  def cancelWhen[B](expect: Expect[B]): Scenario[F, A] =
    scenario.cancelOn(expect.isDefinedAt)
}
