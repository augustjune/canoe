package com.canoe.telegram.scenarios

import com.canoe.telegram.models.PrivateChat
import com.canoe.telegram.models.messages.{TelegramMessage, TextMessage}
import fs2.{Pure, Stream}
import org.scalatest.FunSuite

class ReceiveSpec extends FunSuite {

  def textMessage(chatId: Long, text: String): TextMessage =
    TextMessage(-1, PrivateChat(chatId, None, "", None), 0, text)

  val expected: String = "fire"

  val predicate: TelegramMessage => Boolean = {
    case m: TextMessage => m.text == expected
    case _              => false
  }

  test("Receive needs at least one message") {
    val scenario: Receive[Pure] = Receive(predicate)
    val input = Stream.empty

    assert(input.through(scenario).toList.isEmpty)
  }

  test("Receive returns all matched occurrences") {
    val scenario: Receive[Pure] = Receive(predicate)
    val input = Stream(
      textMessage(1, expected),
      textMessage(1, expected),
      textMessage(1, ""),
      textMessage(2, expected)
    )

    assert(input.through(scenario).toList.size == input.toList.count(predicate))
  }

  test("Receive.collect handles undefined predicate values") {
    val scenario: Scenario[Pure, Unit] = Receive.collect {
      case m: TextMessage if m.text == expected => ()
    }
    val input = Stream(textMessage(1, ""))

    assert(input.through(scenario).toList.isEmpty)
  }

  test("Scenario.receive handles undefined predicate values") {
    val scenario: Scenario[Pure, TelegramMessage] = Scenario.receive {
      case m: TextMessage if m.text == expected => true
    }
    val input = Stream(textMessage(1, ""))

    assert(input.through(scenario).toList.isEmpty)
  }

  test("Receive.collect maps the result") {
    val chatId = 1
    val scenario: Scenario[Pure, Long] = Receive.collect {
      case m: TextMessage if m.text == expected => m.chat.id
    }
    val input = Stream(textMessage(chatId, expected))

    assert(input.through(scenario).toList.head == chatId)
  }

  test("Receive.any matches any message") {
    val scenario: Scenario[Pure, TelegramMessage] = Receive.any
    val input = Stream(textMessage(1, ""))

    assert(input.through(scenario).toList.nonEmpty)
  }

  test("Receive#or always returns left") {
    val scenario: Scenario[Pure, Either[Long, Long]] =
      Receive(predicate).map(_.chat.id).or(Receive.any.map(_.chat.id))

    val input = Stream(
      textMessage(1, expected),
      textMessage(2, ""),
      textMessage(3, expected)
    )

    assert(input.through(scenario).toList == List(Left(1), Left(3)))
  }
}
