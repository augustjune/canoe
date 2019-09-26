package canoe

import canoe.models.messages.{TelegramMessage, TextMessage}

package object syntax extends Contents with Expects {

  type Expect[A] = PartialFunction[TelegramMessage, A]

  implicit def partialFunctionOps[A, B](original: PartialFunction[A, B]): PartialFunctionOps[A, B] =
    new PartialFunctionOps[A, B](original)

  implicit def expectTelegramMessageOps(original: Expect[TelegramMessage]): ExpectTelegramMessageOps =
    new ExpectTelegramMessageOps(original)

  implicit def expectTextMessageOps(textMessage: Expect[TextMessage]): ExpectTextMessageOps =
    new ExpectTextMessageOps(textMessage)

  implicit def methodOps[A](a: A): MethodSyntax[A] = new MethodSyntax[A](a)
}
