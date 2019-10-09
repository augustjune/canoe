package canoe.api.matching

import canoe.api.matching.EpisodeCheckInstances._
import cats.Monad
import cats.effect.IO
import cats.implicits._
import cats.laws.discipline._
import org.scalatest.funsuite.AnyFunSuite
import org.typelevel.discipline.scalatest.Discipline

class EpisodeLawsCheck extends AnyFunSuite with Discipline {
  // By re-declaring implicit Monad instance here we ensure that it will be used during monad tests
  // instead of MonadError instance
  implicit def monadInstance[F[_], I]: Monad[Episode[F, I, *]] = Episode.monadInstance
  checkAll("Episode.Monad laws", MonadTests[Episode[IO, Int, *]].monad[Int, Int, Int])

  checkAll("Episode.MonadError laws", MonadErrorTests[Episode[IO, Int, *], Throwable].monadError[Int, Int, Int])
}
