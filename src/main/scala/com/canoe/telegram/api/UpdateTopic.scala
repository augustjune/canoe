package com.canoe.telegram.api

import com.canoe.telegram.models.Update
import fs2.Stream

trait UpdateTopic[F[_]] {

  /**
    * Forks a new stream of ongoing updates
    * Doesn't require any additional work for new forks
    */
  def fork: Stream[F, Update]

  /**
    * Starts populating the topic with updates
    */
  def start: Stream[F, Unit]
}
