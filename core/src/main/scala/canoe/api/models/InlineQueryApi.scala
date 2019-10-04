package canoe.api.models

import canoe.api.TelegramClient
import canoe.methods.queries.AnswerInlineQuery
import canoe.models.{InlineQuery, InlineQueryResult}
import canoe.syntax.methodOps

final class InlineQueryApi[F[_]](query: InlineQuery)(implicit client: TelegramClient[F]) {

  /**
    * Sends the answer to this query
    *
    * @param results No more than 50 results per query are allowed
    */
  def answer(results: List[InlineQueryResult],
             switchPmText: Option[String] = None,
             switchPmParameter: Option[String] = None): F[Boolean] =
    AnswerInlineQuery(query.id, results, switchPmText = switchPmText, switchPmParameter = switchPmParameter).call
}
