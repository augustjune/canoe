package com.canoe.telegram.scenarios

import cats.effect.{ExitCode, IO, IOApp, Sync}
import fs2.{Pull, Stream}

import scala.util.Random

object PullScenario extends IOApp {

  case class Scenario(steps: List[String]) {
    def stateful: StatefulScenario = StatefulScenario(this, steps)
  }

  case class StatefulScenario(scenario: Scenario, left: List[String]) {
    def reset: StatefulScenario = copy(left = scenario.steps)

    def next: StatefulScenario = copy(left = left.tail)

    def isFull: Boolean = scenario.steps.size == left.size
  }

  def ints[F[_] : Sync](n: Int): Stream[F, Int] =
    Stream.repeatEval(Sync[F].delay(Random.nextInt(n)))

  case class Finish(texts: List[String], chatId: Int, endIndex: Long)

  case class Message(chatId: Int, text: String)

  def runF[F[_]] = {

    def fulfill(input: Stream[F, (Int, String)], scenario: Scenario): Stream[F, Finish] = {
      def loop(s: Stream[F, ((Int, String), Long)], scenarios: Map[Int, StatefulScenario]): Pull[F, Finish, Unit] = {
        s.pull.uncons1.flatMap {
          case Some((((id, text), index), rest)) =>

            scenarios.get(id) match {
              case None =>
                scenario.steps match {
                  case Nil => Pull.output1(Finish(Nil, id, index)) >> loop(rest, scenarios)
                  case List(x) =>
                    if (text == x) Pull.output1(Finish(List(x), id, index)) >> loop(rest, scenarios)
                    else loop(rest, scenarios)

                  case h :: _ =>
                    if (text == h) loop(rest, scenarios.updated(id, scenario.stateful.next))
                    else loop(rest, scenarios)
                }

              case Some(statefulScenario) =>
                statefulScenario.left match {

                  case List(m) =>
                    if (m == text)
                      Pull.output1(
                        Finish(statefulScenario.scenario.steps, id, index)) >>
                        loop(rest, scenarios.updated(id, statefulScenario.reset))
                    else if (statefulScenario.isFull) loop(rest, scenarios.updated(id, statefulScenario.reset))
                    else loop(rest.cons1(((id, text), index)), scenarios.updated(id, statefulScenario.reset))

                  case h :: _ =>
                    if (h == text) loop(rest, scenarios.updated(id, statefulScenario.next))
                    else if (statefulScenario.isFull) loop(rest, scenarios.updated(id, statefulScenario.reset))
                    else loop(rest.cons1(((id, text), index)), scenarios.updated(id, statefulScenario.reset))
                }
            }

          case None => Pull.done
        }
      }

      loop(input.zipWithIndex, Map.empty[Int, StatefulScenario]).stream
    }

    fulfill(Stream(
      (2, "1"),
      (1, "1"),
      (2, "2"),
      (2, "3"),
      (2, "1"),
      (2, "2"),
      (2, "1"),
      (2, "2"),
      (2, "3"),
      (1, "1"),
      (1, "2"),
      (1, "3"),
      (1, "4"),
      (1, "1"),
      (1, "2"),
      (1, "3")),
      Scenario(List("1", "2", "3")))
  }

  import cats.syntax.all._

  def run(args: List[String]): IO[ExitCode] =
    runF[IO].compile.toList.map(println).as(ExitCode.Success)
}
