package com.canoe.telegram.methods.chats

import com.canoe.telegram.methods.JsonRequest
import com.canoe.telegram.models.{ChatId, ChatMember}

/** Use this method to get information about a member of a chat. Returns a ChatMember object on success.
  *
  * @param chatId Integer or String Unique identifier for the target chat or username of the target supergroup or channel (in the format @channelusername)
  * @param userId Integer Unique identifier of the target user
  */
case class GetChatMember(chatId: ChatId, userId: Int) extends JsonRequest[ChatMember]
