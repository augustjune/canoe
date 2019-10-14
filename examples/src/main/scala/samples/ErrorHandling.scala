package samples

import canoe.api._
import canoe.syntax._
import cats.effect.{ExitCode, IO, IOApp, Sync}
import cats.syntax.all._
import fs2.Stream

object ErrorHandling extends IOApp {

  val token: String = "<your telegram token>"

  trait FaultyService[F[_]] {

    /**
      * Produces order id for each ordered item.
      * May fail if there's no items left, if the service is unavailable or because it's rainy outside.
      */
    def order(item: String): F[Long]
  }

  val amazon: FaultyService[IO] = ???

  def run(args: List[String]): IO[ExitCode] =
    Stream
      .resource(TelegramClient.global[IO](token))
      .flatMap { implicit client =>
        Bot.polling[IO].follow(order(amazon))
      }
      .compile.drain.as(ExitCode.Success)

  def order[F[_]: TelegramClient: Sync](service: FaultyService[F]): Scenario[F, Unit] =
    for {
      chat   <- Scenario.start(command("order").chat)
      _      <- Scenario.eval(chat.send("What you'd like to order?"))
      item   <- Scenario.next(text)
      result <- Scenario.eval(service.order(item)).attempt
      _ <- result.fold(
        e => Scenario.eval(processError(e)).flatMap(m => Scenario.eval(chat.send(m))),
        orderId => Scenario.eval(chat.send(s"Order successfully made. Here's your order id: $orderId"))
      )
    } yield ()

  /**
    * Store/process the error and produce user message.
    */
  def processError[F[_]: Sync](e: Throwable): F[String] =
    Sync[F].pure("Something went wrong while making your order. Please try again.")
}
