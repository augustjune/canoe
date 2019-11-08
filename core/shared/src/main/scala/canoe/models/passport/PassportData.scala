package canoe.models.passport

/**
  * Telegram Passport data shared with the bot by the user
  *
  * @param data        Documents and other Telegram Passport elements that was shared with the bot
  * @param credentials Encrypted credentials required to decrypt the data
  */
final case class PassportData(data: List[EncryptedPassportElement], credentials: EncryptedCredentials)
