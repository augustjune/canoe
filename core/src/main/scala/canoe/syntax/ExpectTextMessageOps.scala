package canoe.syntax

import canoe.models.messages.TextMessage

final class ExpectTextMessageOps(private val textMessage: Expect[TextMessage]) extends AnyVal {

  def containing(substring: String): Expect[TextMessage] =
    textMessage.when(_.text.contains(substring))

  def startingWith(prefix: String): Expect[TextMessage] =
    textMessage.when(_.text.startsWith(prefix))

  def endingWith(ending: String): Expect[TextMessage] =
    textMessage.when(_.text.endsWith(ending))

  def matching(regex: String): Expect[TextMessage] =
    textMessage.when(_.text.matches(regex))
}
