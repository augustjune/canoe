package canoe.api.models

import canoe.clients.RequestHandler
import canoe.methods.queries.AnswerInlineQuery
import canoe.models.{InlineQuery, InlineQueryResult}

final class InlineQueryApi[F[_]](query: InlineQuery)
                          (implicit client: RequestHandler[F]) {

  def answer(results: Seq[InlineQueryResult],
             switchPmText: Option[String] = None,
             switchPmParameter: Option[String] = None): F[Boolean] =
    client.execute(AnswerInlineQuery(query.id, results, switchPmText = switchPmText, switchPmParameter = switchPmParameter))
}
