package com.canoe.telegram.methods.messages

import com.canoe.telegram.methods.JsonRequest
import com.canoe.telegram.models.ReplyMarkup
import com.canoe.telegram.models.messages.TelegramMessage

/** Use this method to send a game.
  * On success, the sent Message is returned.
  *
  * @param chatId              Integer	Yes	Unique identifier for the target chat
  * @param gameShortName       String Short name of the game, serves as the unique identifier for the game. Set up your games via Botfather.
  * @param disableNotification Boolean Optional Sends the message silently.
  *                            iOS users will not receive a notification, Android users will receive a notification with no sound.
  * @param replyToMessageId    Integer Optional If the message is a reply, ID of the original message
  * @param replyMarkup         InlineKeyboardMarkup Optional A JSON-serialized object for an inline keyboard.
  *                            If empty, one 'Play game_title' button will be shown.
  *                            If not empty, the first button must launch the game.
  */
case class SendGame(chatId: Long,
                    gameShortName: String,
                    disableNotification: Option[Boolean] = None,
                    replyToMessageId: Option[Int] = None,
                    replyMarkup: Option[ReplyMarkup] = None
                   ) extends JsonRequest[TelegramMessage]
