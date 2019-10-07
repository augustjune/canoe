package canoe.api.models

import canoe.api.TelegramClient
import canoe.methods.messages.{EditMessageLiveLocation, StopMessageLiveLocation}
import canoe.models.Location
import canoe.models.messages.{LocationMessage, TelegramMessage}
import canoe.syntax.methodOps

final class LiveLocationAPI[F[_]: TelegramClient](message: LocationMessage) {

  /**
    * Edits live location message with a new location.
    * This can only be done while live location is active.
    */
  def editLiveLocation(location: Location): F[Either[Boolean, TelegramMessage]] =
    EditMessageLiveLocation.direct(message.chat.id, message.messageId, location.latitude, location.longitude).call

  /**
    * Stops updating a live location message.
    */
  def stopLiveLocation: F[Either[Boolean, TelegramMessage]] =
    StopMessageLiveLocation.direct(message.chat.id, message.messageId).call
}
