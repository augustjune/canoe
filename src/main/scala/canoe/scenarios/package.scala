package canoe

import canoe.models.messages.TelegramMessage

package object scenarios {

  type Interaction[F[_], A] = Scenario[F, TelegramMessage, A]

}
