package canoe.api

import canoe.models.Update
import fs2.Stream

private[api] trait UpdateSource[F[_]] {
  /**
    * Incoming Telegram updates.
    */
  def updates: Stream[F, Update]
}
