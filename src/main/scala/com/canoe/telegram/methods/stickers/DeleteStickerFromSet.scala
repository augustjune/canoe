package com.canoe.telegram.methods.stickers

import com.canoe.telegram.methods.JsonRequest

/**
  * Use this method to delete a sticker from a set created by the bot.
  * Returns True on success.
  *
  * @param sticker String	File identifier of the sticker
  */
case class DeleteStickerFromSet(sticker: String) extends JsonRequest[Boolean]
