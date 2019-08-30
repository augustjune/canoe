package canoe.models.messages

import canoe.models.{Chat, SuccessfulPayment}

case class SuccessfulPaymentMessage(messageId: Int, chat: Chat, date: Int, successfulPayment: SuccessfulPayment)
    extends TelegramMessage
