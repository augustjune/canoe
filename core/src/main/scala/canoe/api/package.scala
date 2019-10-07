package canoe

import canoe.api.models._
import canoe.models.messages.{LocationMessage, PollMessage, TelegramMessage}
import canoe.models._

package object api {

  implicit def chatApi[F[_]: TelegramClient](chat: Chat): ChatApi[F] =
    new ChatApi[F](chat)

  implicit def messageApi[F[_]: TelegramClient](message: TelegramMessage): MessageApi[F] =
    new MessageApi[F](message)

  implicit def pollApi[F[_]: TelegramClient](message: PollMessage): PollMessageApi[F] =
    new PollMessageApi[F](message)

  implicit def liveLocationApi[F[_]: TelegramClient](message: LocationMessage): LiveLocationAPI[F] =
    new LiveLocationAPI[F](message)

  implicit def inlineQueryApi[F[_]: TelegramClient](query: InlineQuery): InlineQueryApi[F] =
    new InlineQueryApi[F](query)

  implicit def callbackQueryApi[F[_]: TelegramClient](query: CallbackQuery): CallbackQueryAPI[F] =
    new CallbackQueryAPI[F](query)

  implicit def preCheckoutQueryApi[F[_]: TelegramClient](query: PreCheckoutQuery): PreCheckoutQueryAPI[F] =
    new PreCheckoutQueryAPI[F](query)

  implicit def shippingQueryApi[F[_]: TelegramClient](query: ShippingQuery): ShippingQueryAPI[F] =
    new ShippingQueryAPI[F](query)
}
