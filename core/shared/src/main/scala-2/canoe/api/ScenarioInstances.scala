package canoe.api

import cats.arrow.FunctionK
import cats.{MonadThrow, StackSafeMonad, ~>}

trait ScenarioInstances {

  implicit def scenarioMonadThrowInstance[F[_]]: MonadThrow[Scenario[F, *]] =
    new MonadThrow[Scenario[F, *]] with StackSafeMonad[Scenario[F, *]] {

      def pure[A](x: A): Scenario[F, A] =
        Scenario.pure(x)

      def raiseError[A](e: Throwable): Scenario[F, A] =
        Scenario.raiseError(e)

      def handleErrorWith[A](fa: Scenario[F, A])(f: Throwable => Scenario[F, A]): Scenario[F, A] =
        fa.handleErrorWith(f)

      def flatMap[A, B](fa: Scenario[F, A])(f: A => Scenario[F, B]): Scenario[F, B] =
        fa.flatMap(f)
    }

  implicit def scenarioFunctionKInstance[F[_]]: F ~> Scenario[F, *] =
    FunctionK.lift(Scenario.eval)
}
