package canoe.api

import canoe.models.messages.{TelegramMessage, TextMessage}

package object syntax extends Contents with Expects {

  type Expect[A, B] = PartialFunction[A, B]
  type ExpectMessage[A] = Expect[TelegramMessage, A]

  implicit def expectImplicitOps[A, B](original: Expect[A, B]): ExpectOps[A, B] =
    new ExpectOps[A, B](original)

  implicit def expectTelegramMessageOps(original: ExpectMessage[TelegramMessage]): ExpectTelegramMessageOps =
    new ExpectTelegramMessageOps(original)

  implicit def expectTextMessageOps(textMessage: ExpectMessage[TextMessage]): ExpectTextMessageOps =
    new ExpectTextMessageOps(textMessage)
}
