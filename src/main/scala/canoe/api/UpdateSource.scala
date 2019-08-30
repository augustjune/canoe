package canoe.api

import canoe.models.Update
import fs2.Stream

trait UpdateSource[F[_]] {

  def updates: Stream[F, Update]
}
