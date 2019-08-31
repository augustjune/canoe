package canoe.api.syntax

import canoe.models.messages.{TelegramMessage, TextMessage}

final class ExpectTextMessageOps(private val textMessage: ExpectMessage[TextMessage]) extends AnyVal {

  private def when(p: TextMessage => Boolean): ExpectMessage[TextMessage] =
    new ExpectMessage[TextMessage] {
      def isDefinedAt(a: TelegramMessage): Boolean =
        textMessage.isDefinedAt(a) && p(textMessage.apply(a))

      def apply(a: TelegramMessage): TextMessage = textMessage.apply(a)
    }

  def contains(substring: String): ExpectMessage[TextMessage] =
    when(_.text.contains(substring))

  def startsWith(prefix: String): ExpectMessage[TextMessage] =
    when(_.text.startsWith(prefix))

  def endsWith(ending: String): ExpectMessage[TextMessage] =
    when(_.text.endsWith(ending))
}
