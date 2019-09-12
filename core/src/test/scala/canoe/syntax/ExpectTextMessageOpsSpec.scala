package canoe.syntax

import canoe.models.PrivateChat
import canoe.models.messages.{TelegramMessage, TextMessage}
import org.scalatest.FunSuite

class ExpectTextMessageOpsSpec extends FunSuite {

  val anyTextMessage: Expect[TextMessage] = canoe.syntax.textMessage

  def message(text: String): TelegramMessage =
    TextMessage(-1, PrivateChat(-1, None, None, None), -1, text)

  test("#startingWith is defined only at messages which texts are starting with provided string") {
    val expect = anyTextMessage.startingWith("start")
    assert(expect.isDefinedAt(message("start dadada")))
    assert(!expect.isDefinedAt(message("ad")))
  }

  test("#endingWith is defined only at messages which texts are ending with provided string") {
    val expect = anyTextMessage.endingWith("end")
    assert(expect.isDefinedAt(message("adasdend")))
    assert(!expect.isDefinedAt(message("ad")))
  }

  test("#containing is defined only at messages which texts contain provided string") {
    val expect = anyTextMessage.containing("sub")
    assert(expect.isDefinedAt(message("adassubdend")))
    assert(!expect.isDefinedAt(message("ad")))
  }

  test("#matching is defined only at messages which texts match provided regex") {
    val expect = anyTextMessage.matching("[a-z]*")
    assert(expect.isDefinedAt(message("adassubdend")))
    assert(!expect.isDefinedAt(message("aTd")))
  }
}
