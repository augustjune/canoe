package canoe

import canoe.api.models._
import canoe.models._
import canoe.models.messages.{LocationMessage, PollMessage, TelegramMessage}

/**
  * Contains implicit conversions to the API classes of particular Telegram models
  */
package object api {

  implicit def chatApi(chat: Chat): ChatApi =
    new ChatApi(chat)

  implicit def messageApi(message: TelegramMessage): MessageApi =
    new MessageApi(message)

  implicit def pollApi(message: PollMessage): PollMessageApi =
    new PollMessageApi(message)

  implicit def liveLocationApi(message: LocationMessage): LiveLocationAPI =
    new LiveLocationAPI(message)

  implicit def inlineQueryApi(query: InlineQuery): InlineQueryApi =
    new InlineQueryApi(query)

  implicit def callbackQueryApi(query: CallbackQuery): CallbackQueryAPI =
    new CallbackQueryAPI(query)

  implicit def preCheckoutQueryApi(query: PreCheckoutQuery): PreCheckoutQueryAPI =
    new PreCheckoutQueryAPI(query)

  implicit def shippingQueryApi(query: ShippingQuery): ShippingQueryAPI =
    new ShippingQueryAPI(query)
}
