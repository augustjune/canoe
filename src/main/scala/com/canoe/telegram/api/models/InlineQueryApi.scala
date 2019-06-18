package com.canoe.telegram.api.models

import com.canoe.telegram.clients.RequestHandler
import com.canoe.telegram.methods.queries.AnswerInlineQuery
import com.canoe.telegram.models.{InlineQuery, InlineQueryResult}

final class InlineQueryApi[F[_]](query: InlineQuery)
                          (implicit client: RequestHandler[F]) {

  def answer(results: Seq[InlineQueryResult],
             switchPmText: Option[String] = None,
             switchPmParameter: Option[String] = None): F[Boolean] =
    client.execute(AnswerInlineQuery(query.id, results, switchPmText = switchPmText, switchPmParameter = switchPmParameter))
}
