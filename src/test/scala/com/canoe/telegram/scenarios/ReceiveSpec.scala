package com.canoe.telegram.scenarios

import com.canoe.telegram.models.PrivateChat
import com.canoe.telegram.models.messages.{TelegramMessage, TextMessage}
import fs2.{Pure, Stream}
import org.scalatest.FunSuite

class ReceiveSpec extends FunSuite {

  def textMessage(chatId: Long, text: String): TextMessage =
    TextMessage(-1, PrivateChat(chatId, None, "", None), 0, text)

  val expected: String = "fire"

  val predicate: PartialFunction[TelegramMessage, TelegramMessage] = {
    case m: TextMessage if m.text == expected => m
  }

  test("Receive needs at least one message") {
    val scenario: Scenario[Pure, TelegramMessage, TelegramMessage] = ChatScenario.start(predicate)
    val input = Stream.empty

    assert(input.through(scenario).toList.isEmpty)
  }

  test("Receive returns all matched occurrences") {
    val scenario: Scenario[Pure, TelegramMessage, TelegramMessage] = ChatScenario.start(predicate)
    val input = Stream(
      textMessage(1, expected),
      textMessage(1, expected),
      textMessage(1, ""),
      textMessage(2, expected)
    )

    //assert(input.through(scenario).toList.size == input.toList.count(predicate))
  }

  test("Receive.collect handles undefined predicate values") {
    val scenario: Scenario[Pure, TelegramMessage, Unit] = ChatScenario.start {
      case m: TextMessage if m.text == expected => ()
    }
    val input = Stream(textMessage(1, ""))

    assert(input.through(scenario).toList.isEmpty)
  }

  test("Scenario.receive handles undefined predicate values") {
    val scenario: Scenario[Pure, TelegramMessage, TelegramMessage] = ChatScenario.start {
      case m: TextMessage if m.text == expected => m
    }
    val input = Stream(textMessage(1, ""))

    assert(input.through(scenario).toList.isEmpty)
  }

  test("Receive.collect maps the result") {
    val chatId = 1
    val scenario: Scenario[Pure, TelegramMessage, Long] = ChatScenario.start {
      case m: TextMessage if m.text == expected => m.chat.id
    }
    val input = Stream(textMessage(chatId, expected))

    assert(input.through(scenario).toList.head == chatId)
  }

  test("Receive.any matches any message") {
    val scenario: Scenario[Pure, TelegramMessage, TelegramMessage] = ChatScenario.start { case m => m }
    val input = Stream(textMessage(1, ""))

    assert(input.through(scenario).toList.nonEmpty)
  }
}
