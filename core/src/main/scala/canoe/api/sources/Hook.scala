package canoe.api.sources

import canoe.api.UpdateSource
import canoe.models.Update
import cats.effect.{ConcurrentEffect, Resource, Timer}
import cats.syntax.all._
import fs2.Stream
import fs2.concurrent.Queue
import org.http4s._
import org.http4s.circe.jsonOf
import org.http4s.dsl.Http4sDsl
import org.http4s.implicits._
import org.http4s.server.Server
import org.http4s.server.blaze.BlazeServerBuilder

class Hook[F[_]](queue: Queue[F, Update]) extends UpdateSource[F] {
  def updates: Stream[F, Update] = queue.dequeue
}

object Hook {

  /**
    * Creates a local server which listens for the incoming updates on provided `port`.
    */
  def create[F[_]: ConcurrentEffect: Timer](port: Int): Resource[F, Hook[F]] = {
    val dsl = Http4sDsl[F]
    import dsl._

    def app(queue: Queue[F, Update]): HttpApp[F] =
      HttpRoutes
        .of[F] {
          case req @ POST -> Root =>
            req
              .decodeWith(jsonOf[F, Update], strict = true)(queue.enqueue1(_) *> Ok())
              .recoverWith {
                // ToDo - signal the error
                case _: InvalidMessageBodyFailure => Ok()
              }
        }
        .orNotFound

    def server(queue: Queue[F, Update]): Resource[F, Server[F]] =
      BlazeServerBuilder[F].bindHttp(port).withHttpApp(app(queue)).resource

    Resource.suspend(Queue.unbounded[F, Update].map(q => server(q).map(_ => new Hook[F](q))))
  }
}
