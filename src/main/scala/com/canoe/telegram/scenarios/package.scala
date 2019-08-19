package com.canoe.telegram

import com.canoe.telegram.models.messages.TelegramMessage

package object scenarios {

  type Interaction[F[_], A] = Scenario[F, TelegramMessage, A]

}
