package samples

import canoe.api._
import canoe.models.InlineQuery
import cats.effect.{ExitCode, IO, IOApp}
import cats.syntax.functor._
import fs2.Stream

/**
  * Example of defining behavior of the bot without using Scenario syntax.
  *
  * Each incoming inline query will be answered with applied method.
  */
object NoScenario extends IOApp {

  val token: String = "<your telegram token>"

  def run(args: List[String]): IO[ExitCode] =
    Stream
      .resource(TelegramClient.global[IO](token))
      .flatMap { implicit client =>
        Bot.polling[IO].updates.through(pipes.inlineQueries).evalTap(answerInlineQuery(_).void)
      }
      .compile.drain.as(ExitCode.Success)

  def answerInlineQuery[F[_]: TelegramClient](query: InlineQuery): F[Boolean] =
    query.answer(results = List())
}
