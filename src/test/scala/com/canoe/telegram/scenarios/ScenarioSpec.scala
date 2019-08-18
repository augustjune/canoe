package com.canoe.telegram.scenarios

import cats.Id
import cats.effect.IO
import com.canoe.telegram.models.PrivateChat
import com.canoe.telegram.models.messages.{TelegramMessage, TextMessage}
import org.scalatest.FunSuite
import fs2.{Pure, Stream}

class ScenarioSpec extends FunSuite {

  implicit class IdStreamOps[A](stream: Stream[Id, A]) {
    def toList: List[A] = stream.covaryId[IO].compile.toList.unsafeRunSync()

    def size: Int = toList.size
  }

  def textMessage(chatId: Long, text: String): TextMessage =
    TextMessage(-1, PrivateChat(chatId, None, "", None), 0, text)

  test("Receive >>= Expect") {
    val scenario: Scenario[Pure, TextMessage, Long] =
      for {
        m <- ChatScenario.start { case m: TextMessage if m.text == "one" => m }
        _ <- ChatScenario.next { case m: TextMessage  if m.text == "two" => m }
      } yield m.chat.id

    val chatId = 12
    val input = Stream(textMessage(chatId, "one"), textMessage(chatId, "two"))

    assert(input.through(scenario).toList.size == 1)
  }

  test("Scenario doesn't ignore the element which is mismatched") {
    val scenario: Scenario[Pure, TelegramMessage, Long] =
      for {
        m <- ChatScenario.start { case m: TextMessage if m.text == "one" => m }
        _ <- ChatScenario.next { case m: TextMessage  if m.text == "two" => m }
      } yield m.chat.id

    val input = Stream(
      textMessage(1, "one"),
      textMessage(2, "one"),
      textMessage(3, "two")
    )

    assert(input.through(scenario).toList.head == 2)
  }
}
