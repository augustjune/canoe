package samples

import canoe.api._
import canoe.models.InlineQuery
import cats.effect.{IO, IOApp}
import cats.syntax.functor._
import fs2.Stream

/** Example of defining behavior of the bot without using Scenario syntax.
  *
  * Each incoming inline query will be answered with applied method.
  */
object NoScenario extends IOApp.Simple {

  val token: String = "<your telegram token>"

  def run: IO[Unit] =
    Stream
      .resource(TelegramClient.global[IO](token))
      .flatMap { implicit client =>
        Bot.polling[IO].updates.through(pipes.inlineQueries).evalTap(answerInlineQuery(_).void)
      }
      .compile
      .drain

  def answerInlineQuery[F[_]: TelegramClient](query: InlineQuery): F[Boolean] =
    query.answer(results = List())
}
