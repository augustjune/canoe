package canoe

import canoe.api.models._
import canoe.models.messages.TelegramMessage
import canoe.models.{Chat, InlineQuery}
import canoe.scenarios.{Episode, ScenarioOps}

package object api {

  /**
    * Description of an interaction between two sides,
    * where generally one is the application (bot) and the other is Telegram user.
    *
    * Particular interaction is coherent with some scenario as long as it matches the description
    * (i.e. every step of the interaction is the same as described in scenario)
    *
    * See Scenario object methods for particular ways of constructing a scenario
    */
  type Scenario[F[_], A] = Episode[F, TelegramMessage, A]

  implicit def scenarioOps[F[_], A](scenario: Scenario[F, A]): ScenarioOps[F, A]=
    new ScenarioOps(scenario)

  implicit def chatApi[F[_]: TelegramClient](chat: Chat): ChatApi[F] =
    new ChatApi[F](chat)

  implicit def messageApi[F[_]: TelegramClient](message: TelegramMessage): MessageApi[F] =
    new MessageApi[F](message)

  implicit def inlineQueryApi[F[_]: TelegramClient](query: InlineQuery): InlineQueryApi[F] =
    new InlineQueryApi[F](query)
}
