package canoe.api.sources

import canoe.TestIO._
import canoe.api.TelegramClient
import canoe.methods.Method
import canoe.methods.updates.GetUpdates
import canoe.models.messages.TextMessage
import canoe.models.{MessageReceived, PrivateChat, Update}
import cats.effect.IO
import org.scalatest.funsuite.AnyFunSuite

class PollingSpec extends AnyFunSuite {

  def updatesClient: TelegramClient[IO] = new TelegramClient[IO] {
    def execute[Req, Res](request: Req)(implicit M: Method[Req, Res]): IO[Res] =
      if (M.name != GetUpdates.method.name) throw new UnsupportedOperationException
      else {
        val getUpdates: GetUpdates = request.asInstanceOf[GetUpdates]
        val update: Update =
          MessageReceived(getUpdates.offset.get, TextMessage(-1, PrivateChat(-1, None, None, None), -1, ""))
        IO.pure(List(update).asInstanceOf[Res])
      }
  }

  val polling = new Polling(updatesClient)

  test("polling starts with given offset") {
    assert(polling.pollUpdates(0).take(1).value().head.updateId == 0)
  }

  test("polling uses last offset increased by 1 for each new call") {
    val updates = polling.pollUpdates(0).zipWithNext
      .collect { case (u1, Some(u2)) => u1.last -> u2.head }
      .take(5)
      .toList()

    assert(updates.forall { case (u1, u2) => u2.updateId == u1.updateId + 1 })
  }
}
