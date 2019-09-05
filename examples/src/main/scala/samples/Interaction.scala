package samples

import canoe.api._
import canoe.syntax._
import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.functor._

/**
  * Example of interaction with user and effective external API
  */
object Interaction extends IOApp {

  val token: String = "<your telegram token>"

  val papaJohns: PizzaPlace[IO] = ???

  def run(args: List[String]): IO[ExitCode] =
    TelegramClient
      .global[IO](token)
      .use { implicit client =>
        Bot.polling[IO].follow(pizzaOrders(papaJohns)).compile.drain
      }
      .as(ExitCode.Success)

  trait PizzaPlace[F[_]] {
    def menu: F[List[String]]

    def order(pizza: String): F[Unit]
  }

  def pizzaOrders[F[_]: TelegramClient](pizzaPlace: PizzaPlace[F]): Scenario[F, Unit] =
    for {
      chat    <- Scenario.start(command("pizza").chat)
      pizzas  <- Scenario.eval(pizzaPlace.menu)
      _       <- Scenario.eval(chat.send(s"We have $pizzas. What kind of pizza you'd like?"))
      order   <- Scenario.next(text)
      _ <-
        if (pizzas.contains(order)) Scenario.eval(pizzaPlace.order(order)) >> Scenario.eval(chat.send("Done."))
        else Scenario.eval(chat.send("Sorry, we don't serve that kind of pizza."))
    } yield ()
}
