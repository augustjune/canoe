package com.canoe.telegram.scenarios

import cats.Id
import cats.effect.IO
import com.canoe.telegram.models.PrivateChat
import com.canoe.telegram.models.messages.{TelegramMessage, TextMessage}
import fs2.{Pure, Stream}
import org.scalatest.FunSuite

class ExpectSpec extends FunSuite {

  implicit class IdStreamOps[A](stream: Stream[Id, A]) {
    def toList: List[A] = stream.covaryId[IO].compile.toList.unsafeRunSync()

    def size: Int = toList.size
  }

  def textMessage(chatId: Long, text: String): TextMessage =
    TextMessage(-1, PrivateChat(chatId, None, "", None), 0, text)

  val expected: String = "fire"

  val predicate: PartialFunction[TelegramMessage, TelegramMessage] = {
    case m: TextMessage if m.text == expected => m
  }

  test("Expect needs at least one message") {
    val scenario: Scenario[Pure, TelegramMessage, TelegramMessage] = ChatScenario.start(predicate)
    val input = Stream.empty

    assert(input.through(scenario).toList.isEmpty)
  }

  test("Expect matches only the first message") {
    val scenario: Scenario[Pure, TelegramMessage, TelegramMessage] = ChatScenario.next(predicate)

    val input = Stream(textMessage(1, expected), textMessage(2, expected))

    val results = input.through(scenario).toList
    assert(results.size == 1)
    assert(results.head.chat.id == 1)
  }

  test("Expect uses provided predicate to match the result") {
    val scenario: Scenario[Pure, TelegramMessage, TelegramMessage] = ChatScenario.next(predicate)
    val input = Stream(textMessage(1, ""))

    assert(input.through(scenario).toList.isEmpty)
  }

  test("Expect.collect handles undefined predicate values") {
    val scenario: Scenario[Pure, TelegramMessage, Unit] = ChatScenario.next {
      case m: TextMessage if m.text == expected => ()
    }
    val input = Stream(textMessage(1, ""))

    assert(input.through(scenario).toList.isEmpty)
  }

  test("Scenario.expect handles undefined predicate values") {
    val scenario: Scenario[Pure, TelegramMessage, TelegramMessage] = ChatScenario.next {
      case m: TextMessage if m.text == expected => m
    }
    val input = Stream(textMessage(1, ""))

    assert(input.through(scenario).toList.isEmpty)
  }

  test("Expect.collect maps the result") {
    val chatId = 1
    val scenario: Scenario[Pure, TelegramMessage, Long] = ChatScenario.next {
      case m: TextMessage if m.text == expected => m.chat.id
    }
    val input = Stream(textMessage(chatId, expected))

    assert(input.through(scenario).toList.head == chatId)
  }

  test("Expect.any matches any message") {
    val scenario: Scenario[Pure, TelegramMessage, TelegramMessage] = ChatScenario.next { case m => m }
    val input = Stream(textMessage(1, ""))

    assert(input.through(scenario).toList.nonEmpty)
  }

  test("Expect#tolerate doesn't skip the element if it matches") {
    val scenario: Scenario[Id, TelegramMessage, Long] =
      ChatScenario.next(predicate).map(_.chat.id).tolerate(_ => (): Id[Unit])

    val input = Stream(textMessage(1, expected), textMessage(2, expected))

    assert(input.through(scenario).toList.head == 1)
  }

  test("Expect#tolerate skips the element if it doesn't match") {
    val scenario: Scenario[Id, TelegramMessage, Long] =
      ChatScenario.next(predicate).map(_.chat.id).tolerate(_ => (): Id[Unit])

    val input = Stream(textMessage(1, ""), textMessage(2, expected))

    assert(input.through(scenario).toList.head == 2)
  }

  test("Expect#tolerateN skips up to N elements if they don't match") {
    val n = 5
    val scenario: Scenario[Id, TelegramMessage, Long] =
      ChatScenario.next(predicate).map(_.chat.id).tolerateN(n)(_ => (): Id[Unit])

    val input = Stream(textMessage(1, "")).repeatN(5) ++ Stream(
      textMessage(2, expected)
    )

    assert(input.through(scenario).toList.head == 2)
  }

}
