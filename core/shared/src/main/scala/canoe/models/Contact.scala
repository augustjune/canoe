package canoe.models

/**
  * Represents a phone contact.
  *
  * @param phoneNumber Contact's phone number
  * @param firstName   Contact's first name
  * @param lastName    Contact's last name
  * @param userId      Contact's user identifier in Telegram
  * @param vcard       Additional data about the contact in the form of a [[https://en.wikipedia.org/wiki/VCard vCard]]
  */
final case class Contact(phoneNumber: String,
                         firstName: String,
                         lastName: Option[String],
                         userId: Option[Int],
                         vcard: Option[String])
