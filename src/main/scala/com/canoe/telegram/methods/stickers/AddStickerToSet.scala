package com.canoe.telegram.methods.stickers

import com.canoe.telegram.methods.MultipartRequest
import com.canoe.telegram.models.{InputFile, MaskPosition}

/**
  * Use this method to add a new sticker to a set created by the bot.
  * Returns True on success.
  *
  * @param userId       User identifier of sticker set owner
  * @param name         Sticker set name
  * @param pngSticker   Png image with the sticker, must be up to 512 kilobytes in size, dimensions must not exceed 512px, and either width or height must be exactly 512px. Pass a file_id as a String to send a file that already exists on the Telegram servers, pass an HTTP URL as a String for Telegram to get a file from the Internet, or upload a new one using multipart/form-data. More info on Sending Files »
  * @param emojis       One or more emoji corresponding to the sticker
  * @param maskPosition Optional Position where the mask should be placed on faces
  */
case class AddStickerToSet(userId: Int,
                           name: String,
                           pngSticker: InputFile,
                           emojis: String,
                           maskPosition: Option[MaskPosition] = None
                          ) extends MultipartRequest[Boolean] {
  override def getFiles: List[(String, InputFile)] = List("pngSticker" -> pngSticker)
}
