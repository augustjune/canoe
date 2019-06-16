package com.canoe.telegram.api

import com.canoe.telegram.models.Update
import fs2.Stream

trait UpdateTopic[F[_]] {

  /**
    * Forks a stream of ongoing updates
    */
  def fork: Stream[F, Update]

  /**
    * Starts populating the topic
    * BLOCKING STUFF
    */
  def start: F[Unit]
}
