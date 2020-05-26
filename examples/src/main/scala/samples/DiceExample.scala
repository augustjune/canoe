package samples

import canoe.api._
import canoe.methods.messages.SendDice
import canoe.models.messages.DiceThrownMessage
import canoe.models.{Darts, Dice}
import canoe.syntax._
import cats.effect.{ExitCode, IO, IOApp, Sync}
import fs2.Stream

/**
  * Example of echos bot that will send both possible dice messages in response
  */
object DiceExample extends IOApp {
  val token: String = "<your telegram token>"

  def run(args: List[String]): IO[ExitCode] =
    Stream
      .resource(TelegramClient.global[IO](token))
      .flatMap { implicit client =>
        Bot.polling[IO].follow(echos)
      }
      .compile
      .drain
      .as(ExitCode.Success)

  def echos[F[_]: Sync: TelegramClient]: Scenario[F, Unit] =
    for {
      msg <- Scenario.expect(any)
      _   <- Scenario.eval(SendDice(msg.chat.id, Dice).call[F, DiceThrownMessage])
      _   <- Scenario.eval(SendDice(msg.chat.id, Darts).call[F, DiceThrownMessage])
    } yield ()

}
