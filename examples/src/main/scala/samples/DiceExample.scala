package samples

import canoe.api._
import canoe.models.{Basketball, Darts, Dice}
import canoe.syntax._
import cats.effect.{IO, IOApp}
import fs2.Stream

/**
  * Example of echos bot that will send both possible dice messages in response
  */
object DiceExample extends IOApp.Simple {
  val token: String = "<your telegram token>"

  def run: IO[Unit] =
    Stream
      .resource(TelegramClient.global[IO](token))
      .flatMap { implicit client =>
        Bot.polling[IO].follow(echos)
      }
      .compile
      .drain

  def echos[F[_]: TelegramClient]: Scenario[F, Unit] =
    for {
      msg <- Scenario.expect(any)
      _   <- Scenario.eval(msg.chat.send(Dice))
      _   <- Scenario.eval(msg.chat.send(Darts))
      _   <- Scenario.eval(msg.chat.send(Basketball))
    } yield ()

}
