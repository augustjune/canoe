package canoe.models.passport

/**
  * File uploaded to Telegram Passport.
  * Currently all Telegram Passport files are in JPEG format when decrypted and don't exceed 10MB.
  *
  * @param fileId   Identifier for this file
  * @param fileSize File size
  * @param fileDate Unix time when the file was uploaded
  */
final case class PassportFile(fileId: String, fileSize: Int, fileDate: Int)
