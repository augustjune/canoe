package canoe.api.models

import canoe.api.TelegramClient
import canoe.methods.queries.AnswerInlineQuery
import canoe.models.{InlineQuery, InlineQueryResult}
import canoe.syntax.methodOps

/**
  * Telegram API for the inline query.
  * Offers a convenient access to the related Telegram methods in OO style.
  *
  * It is a conscious decision to provide this API via extension methods.
  */
final class InlineQueryApi(private val query: InlineQuery) extends AnyVal {

  /**
    * Sends the answer to this query
    *
    * @param results No more than 50 results per query are allowed
    */
  def answer[F[_]: TelegramClient](results: List[InlineQueryResult],
                                   switchPmText: Option[String] = None,
                                   switchPmParameter: Option[String] = None): F[Boolean] =
    AnswerInlineQuery(query.id, results, switchPmText = switchPmText, switchPmParameter = switchPmParameter).call
}
