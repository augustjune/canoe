package samples

import canoe.api._
import canoe.syntax._
import cats.effect.{IO, IOApp, Sync}
import cats.syntax.all._
import fs2.Stream

/** Example of handling errors withing your scenario.
  *
  * Here `Scenario#attempt` method is used, but the same (and more)
  * may be achieved with `Scenario#handleErrorWith`.
  *
  * Also using `Scenario#raiseError` method you can lift error value
  * in order to represent failing scenario.
  */
object ErrorHandling extends IOApp.Simple {

  val token: String = "<your telegram token>"

  trait FaultyService[F[_]] {

    /** Produces order id for each ordered item.
      * May fail if there's no items left, if the service is unavailable or because it's rainy outside.
      */
    def order(item: String): F[Long]
  }

  val amazon: FaultyService[IO] = ???

  def run: IO[Unit] =
    Stream
      .resource(TelegramClient[IO](token))
      .flatMap { implicit client =>
        Bot.polling[IO].follow(order(amazon))
      }
      .compile
      .drain

  def order[F[_]: TelegramClient: Sync](service: FaultyService[F]): Scenario[F, Unit] =
    for {
      chat   <- Scenario.expect(command("order").chat)
      _      <- Scenario.eval(chat.send("What you'd like to order?"))
      item   <- Scenario.expect(text)
      result <- Scenario.eval(service.order(item)).attempt
      _ <- result.fold(
        e => Scenario.eval(processError(e)).flatMap(m => Scenario.eval(chat.send(m))),
        orderId => Scenario.eval(chat.send(s"Order successfully made. Here's your order id: $orderId"))
      )
    } yield ()

  /** Store/process the error and produce user message.
    */
  def processError[F[_]: Sync](e: Throwable): F[String] =
    Sync[F].pure("Something went wrong while making your order. Please try again.")
}
