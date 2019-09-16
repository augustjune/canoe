package samples

import canoe.api._
import canoe.syntax._
import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.functor._
import fs2.Stream

/**
  * Detailed example of interaction with user and effective external API
  */
object Detailed extends IOApp {

  val token: String = "<your telegram token>"

  val papaJohns: PizzaPlace[IO] = ??? // Let's imagine we have it

  /**
    * Creating TelegramClient in resource context will automatically release it the end of the app,
    * no matter how this app will end (gracefully, raised error, etc.)
    *
    * TelegramClient will be used implicitly later during definition of bot scenario
    */
  def run(args: List[String]): IO[ExitCode] =
    Stream
      .resource(TelegramClient.global[IO](token))
      .flatMap { implicit client =>
        Bot.polling[IO].follow(pizzaOrders(papaJohns))
      }
      .compile.drain.as(ExitCode.Success)

  trait PizzaPlace[F[_]] {
    def menu: F[List[String]]

    def order(pizza: String): F[Unit]
  }

  def pizzaOrders[F[_]: TelegramClient](pizzaPlace: PizzaPlace[F]): Scenario[F, Unit] =
    for {
      chat    <- Scenario.start(command("pizza").chat)    // Defines the start of the scenario with '/pizza' command
      pizzas  <- Scenario.eval(pizzaPlace.menu)           // Suspend effectful evaluation in Scenario context
      _       <- Scenario.eval(chat.send(s"We have $pizzas. What kind of pizza you'd like?"))
      order   <- Scenario.next(text)                      // Defines the next step of the scenario to be a text message
      _ <-                                                // Depending on the user input bot's reaction is different
        if (pizzas.contains(order)) Scenario.eval(pizzaPlace.order(order)) >> Scenario.eval(chat.send("Done."))
        else Scenario.eval(chat.send("Sorry, we don't serve that kind of pizza."))
    } yield ()
}
