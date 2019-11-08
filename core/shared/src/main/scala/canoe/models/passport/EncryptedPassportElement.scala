package canoe.models.passport

sealed trait EncryptedPassportElement extends Product {
  def hash: String
}

object EncryptedPassportElement {

  case class PersonalDetails(hash: String, data: String) extends EncryptedPassportElement

  case class Passport(hash: String,
                      data: String,
                      frontSide: PassportFile,
                      selfie: PassportFile,
                      translation: Option[List[PassportFile]])
      extends EncryptedPassportElement

  case class DriverLicence(hash: String,
                           data: String,
                           frontSide: PassportFile,
                           reverseSide: PassportFile,
                           selfie: PassportFile,
                           translation: Option[List[PassportFile]])
      extends EncryptedPassportElement

  case class IdentityCard(hash: String,
                          data: String,
                          frontSide: PassportFile,
                          reverseSide: PassportFile,
                          selfie: PassportFile,
                          translation: Option[List[PassportFile]])
      extends EncryptedPassportElement

  case class InternalPassport(hash: String,
                              data: String,
                              frontSide: PassportFile,
                              selfie: PassportFile,
                              translation: Option[List[PassportFile]])
      extends EncryptedPassportElement

  case class Address(hash: String, data: String) extends EncryptedPassportElement

  case class UtilityBill(hash: String, files: List[PassportFile], translation: Option[List[PassportFile]])
      extends EncryptedPassportElement

  case class BankStatement(hash: String, files: List[PassportFile], translation: Option[List[PassportFile]])
      extends EncryptedPassportElement

  case class RentalAgreement(hash: String, files: List[PassportFile], translation: Option[List[PassportFile]])
      extends EncryptedPassportElement

  case class PassportRegistration(hash: String, files: List[PassportFile], translation: Option[List[PassportFile]])
      extends EncryptedPassportElement

  case class TemporaryRegistration(hash: String, files: List[PassportFile], translation: Option[List[PassportFile]])
      extends EncryptedPassportElement

  case class PhoneNumber(hash: String, phoneNumber: String) extends EncryptedPassportElement

  case class Email(hash: String, email: String) extends EncryptedPassportElement
}
