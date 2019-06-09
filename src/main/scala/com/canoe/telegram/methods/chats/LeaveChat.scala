package com.canoe.telegram.methods.chats

import com.canoe.telegram.methods.JsonRequest
import com.canoe.telegram.models.ChatId

/** Use this method for your bot to leave a group, supergroup or channel. Returns True on success.
  *
  * @param chatId Integer or String Unique identifier for the target chat or username of the target supergroup or channel (in the format @channelusername)
  */
case class LeaveChat(chatId: ChatId) extends JsonRequest[Boolean]
