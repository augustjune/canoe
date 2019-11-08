package canoe.api.models

import canoe.api.TelegramClient
import canoe.methods.messages.{EditMessageLiveLocation, StopMessageLiveLocation}
import canoe.models.Location
import canoe.models.messages.{LocationMessage, TelegramMessage}
import canoe.syntax.methodOps

/**
  * Telegram API for the live location message.
  * Offers a convenient access to the related Telegram methods.
  */
final class LiveLocationAPI(private val message: LocationMessage) extends AnyVal {

  /**
    * Edits live location message with a new location.
    * This can only be done while live location is active.
    */
  def editLiveLocation[F[_]: TelegramClient](location: Location): F[Either[Boolean, TelegramMessage]] =
    EditMessageLiveLocation.direct(message.chat.id, message.messageId, location.latitude, location.longitude).call

  /**
    * Stops updating a live location message.
    */
  def stopLiveLocation[F[_]: TelegramClient]: F[Either[Boolean, TelegramMessage]] =
    StopMessageLiveLocation.direct(message.chat.id, message.messageId).call
}
