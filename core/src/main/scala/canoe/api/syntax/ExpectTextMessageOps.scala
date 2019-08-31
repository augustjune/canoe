package canoe.api.syntax

import canoe.models.messages.TextMessage

final class ExpectTextMessageOps(private val textMessage: Expect[TextMessage]) extends AnyVal {

  def contains(substring: String): Expect[TextMessage] =
    textMessage.when(_.text.contains(substring))

  def startsWith(prefix: String): Expect[TextMessage] =
    textMessage.when(_.text.startsWith(prefix))

  def endsWith(ending: String): Expect[TextMessage] =
    textMessage.when(_.text.endsWith(ending))
}
