package com.canoe.telegram.models.messages

import com.canoe.telegram.models.{Chat, SuccessfulPayment}

case class SuccessfulPaymentMessage(messageId: Int, chat: Chat, date: Int,
                                    successfulPayment: SuccessfulPayment) extends TelegramMessage
