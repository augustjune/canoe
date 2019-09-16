package canoe.syntax

import canoe.models.messages.TextMessage

final class ExpectTextMessageOps(private val textMessage: Expect[TextMessage]) extends AnyVal {

  /**
    * Narrows this partial function to the text
    * messages which contain `substring` in their texts
    */
  def containing(substring: String): Expect[TextMessage] =
    textMessage.when(_.text.contains(substring))

  /**
    * Narrows this partial function to the text
    * messages which texts start with provided `prefix`
    */
  def startingWith(prefix: String): Expect[TextMessage] =
    textMessage.when(_.text.startsWith(prefix))

  /**
    * Narrows this partial function to the text
    * messages which texts end with provided `ending`
    */
  def endingWith(ending: String): Expect[TextMessage] =
    textMessage.when(_.text.endsWith(ending))

  /**
    * Narrows this partial function to the text
    * messages which texts matches provided regular expression
    */
  def matching(regex: String): Expect[TextMessage] =
    textMessage.when(_.text.matches(regex))
}
