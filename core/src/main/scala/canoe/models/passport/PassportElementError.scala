package canoe.models.passport

/**
  * Error in the Telegram Passport element which was submitted that should be resolved by the user
  */
sealed trait PassportElementError extends Product

/**
  * Issue in one of the data fields that was provided by the user.
  * The error is considered resolved when the field's value changes.
  *
  * @param `type`    The section of the user's Telegram Passport which has the error.
  *                  One of “personal_details”, “passport”, “driver_license”, “identity_card”, “internal_passport”, “address”
  * @param fieldName Name of the data field which has the error
  * @param dataHash  Base64-encoded data hash
  * @param message   Error message
  * @param source    Error source, must be 'data'
  */
final case class PassportElementErrorDataField(`type`: String,
                                               fieldName: String,
                                               dataHash: String,
                                               message: String,
                                               source: String = "data")
    extends PassportElementError

/**
  * Issue with the front side of a document.
  * The error is considered resolved when the file with the front side of the document changes.
  *
  * @param source   Error source, must be 'front_side'
  * @param `type`   The section of the user's Telegram Passport which has the issue.
  *                 One of “passport”, “driver_license”, “identity_card”, “internal_passport”
  * @param fileHash Base64-encoded hash of the file with the front side of the document
  * @param message  Error message
  */
final case class PassportElementErrorFrontSide(`type`: String,
                                               fileHash: String,
                                               message: String,
                                               source: String = "front_side")
    extends PassportElementError

/**
  * Issue with the reverse side of a document.
  * The error is considered resolved when the file with reverse side of the document changes.
  *
  * @param `type`   The section of the user's Telegram Passport which has the issue.
  *                 One of “driver_license”, “identity_card”
  * @param fileHash Base64-encoded hash of the file with the reverse side of the document
  * @param message  Error message
  * @param source   Error source, must be 'reverse_side'
  */
final case class PassportElementErrorReverseSide(`type`: String,
                                                 fileHash: String,
                                                 message: String,
                                                 source: String = "reverse_side")
    extends PassportElementError

/**
  * Issue with the selfie with a document.
  * The error is considered resolved when the file with the selfie
  *
  * @param `type`   The section of the user's Telegram Passport which has the issue,
  *                 One of “passport”, “driver_license”, “identity_card”, “internal_passport”
  * @param fileHash Base64-encoded hash of the file with the selfie
  * @param message  Error message
  * @param source   Error source, must be 'selfie'
  */
final case class PassportElementErrorSelfie(`type`: String,
                                            fileHash: String,
                                            message: String,
                                            source: String = "selfie")
    extends PassportElementError

/**
  * Issue with a document scan.
  * The error is considered resolved when the file with the document scan changes.
  *
  * @param `type`   The section of the user's Telegram Passport which has the issue.
  *                 One of “utility_bill”, “bank_statement”, “rental_agreement”, “passport_registration”, “temporary_registration”
  * @param fileHash Base64-encoded file hash
  * @param message  Error message
  * @param source   Error source, must be 'file'
  */
final case class PassportElementErrorFile(`type`: String, fileHash: String, message: String, source: String = "file")
    extends PassportElementError

/**
  * Issue with a list of scans.
  * The error is considered resolved when the list of files containing the scans changes.
  *
  * @param `type`     The section of the user's Telegram Passport which has the issue.
  *                   One of “utility_bill”, “bank_statement”, “rental_agreement”, “passport_registration”, “temporary_registration”
  * @param fileHashes List of base64-encoded file hashes
  * @param message    Error message
  * @param source     Error source, must be 'files'
  */
final case class PassportElementErrorFiles(`type`: String,
                                           fileHashes: List[String],
                                           message: String,
                                           source: String = "files")
    extends PassportElementError

/**
  * Issue with the translated version of a document.
  * The error is considered resolved when a file with the document translation change.
  *
  * @param source   Error source, must be 'translation_file'
  * @param `type`   Type of element of the user's Telegram Passport which has the issue.
  *                 One of “passport”, “driver_license”, “identity_card”, “internal_passport”, “utility_bill”,
  *                 “bank_statement”, “rental_agreement”, “passport_registration”, “temporary_registration”
  * @param fileHash Base64-encoded file hash
  * @param message  Error message
  */
final case class PassportElementErrorTranslationFile(`type`: String,
                                                     fileHash: String,
                                                     message: String,
                                                     source: String = "translation_file")
    extends PassportElementError

/**
  * Issue with the translated version of a document.
  * The error is considered resolved when a file with the document translation change.
  *
  * @param `type`     Type of element of the user's Telegram Passport which has the issue.
  *                   One of “passport”, “driver_license”, “identity_card”, “internal_passport”, “utility_bill”,
  *                   “bank_statement”, “rental_agreement”, “passport_registration”, “temporary_registration”
  * @param fileHashes List of base64-encoded file hashes
  * @param message    Error message
  * @param source     Error source, must be 'translation_files'
  */
final case class PassportElementErrorTranslationFiles(`type`: String,
                                                      fileHashes: List[String],
                                                      message: String,
                                                      source: String = "translation_files")
    extends PassportElementError

/**
  * Issue in an unspecified place.
  * The error is considered resolved when new data is added.
  *
  * @param `type`       Type of element of the user's Telegram Passport which has the issue
  * @param element_hash Base64-encoded element hash
  * @param message      Error message
  * @param source       Error source, must be 'unspecified'
  */
final case class PassportElementErrorUnspecified(`type`: String,
                                                 element_hash: String,
                                                 message: String,
                                                 source: String = "unspecified")
    extends PassportElementError
