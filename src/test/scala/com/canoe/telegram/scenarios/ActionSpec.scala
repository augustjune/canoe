package com.canoe.telegram.scenarios

import cats.Id
import cats.effect.IO
import com.canoe.telegram.models.messages.TelegramMessage
import org.scalatest.FunSuite
import fs2.{Pure, Stream}

class ActionSpec extends FunSuite {

  implicit class IOStreamOps[A](stream: Stream[IO, A]) {
    def toList(): List[A] = stream.compile.toList.unsafeRunSync()

    def value(): A = toList().head

    def size(): Int = toList().size

    def run(): Unit = stream.compile.drain.unsafeRunSync()
  }

  test("Action doesn't consume any message") {
    val scenario: Scenario[Id, Unit] = Action((): Id[Unit])
    val input: Stream[Pure, TelegramMessage] = Stream.empty

    assert(input.through(scenario).covaryId[IO].size == 1)
  }

  test("Action evaluates effect") {
    var evaluated = false
    val scenario: Scenario[IO, Unit] = Action(IO { evaluated = true })
    val input: Stream[Pure, TelegramMessage] = Stream.empty

    input.through(scenario).run()

    assert(evaluated)
  }

  test("Action evaluates value in an effect") {
    val scenario: Scenario[IO, Int] = Action(IO.pure(1))
    val input: Stream[Pure, TelegramMessage] = Stream.empty

    assert(input.through(scenario).value() == 1)
  }

  test("Action evaluates effect only once") {
    var times = 0
    val scenario: Scenario[IO, Unit] = Action(IO { times = times + 1 })
    val input: Stream[Pure, TelegramMessage] = Stream.empty

    input.through(scenario).run()
    assert(times == 1)
  }

}
