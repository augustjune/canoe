package canoe.api.sources

import canoe.IOSpec
import canoe.api.TelegramClient
import canoe.methods.Method
import canoe.methods.updates.GetUpdates
import canoe.models.messages.TextMessage
import canoe.models.{MessageReceived, PrivateChat, Update}
import cats.effect.IO
import org.scalatest.freespec.AsyncFreeSpec

import scala.concurrent.duration.Duration

class PollingSpec extends AsyncFreeSpec with IOSpec {
  implicit val updatesClient: TelegramClient[IO] = new TelegramClient[IO] {
    def execute[Req, Res](request: Req)(implicit M: Method[Req, Res]): IO[Res] =
      if (M.name != GetUpdates.method.name) throw new UnsupportedOperationException
      else {
        val getUpdates: GetUpdates = request.asInstanceOf[GetUpdates]
        val update: Update =
          MessageReceived(getUpdates.offset.get, TextMessage(-1, PrivateChat(-1, None, None, None), -1, ""))
        IO.pure(List(update).asInstanceOf[Res])
      }
  }

  val polling = new Polling[IO](Duration.Zero)
  "polling" - {
    "starts with given offset" in {
      polling.pollUpdates(0).head.compile.lastOrError.flatMap { updates =>
        IO(assert(updates.head.updateId == 0))
      }
    }

    "uses last offset increased by 1 for each new call" in {
      val updates = polling
        .pollUpdates(0)
        .zipWithNext
        .collect { case (u1, Some(u2)) => u1.last -> u2.head }
        .take(5)
        .compile
        .toList

      updates.flatMap(updates => IO(assert(updates.forall { case (u1, u2) => u2.updateId == u1.updateId + 1 })))

    }
  }
}
