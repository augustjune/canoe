package canoe.methods.passport

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.InputFile
import canoe.models.passport.PassportElementError
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}

/**
  * Informs a user that some of the Telegram Passport elements they provided contains errors.
  * The user will not be able to re-submit their Passport to you until the errors are fixed
  * (the contents of the field for which you returned the error must change).
  *
  * Returns True on success.
  */
final case class SetPassportDataErrors(userId: Int, errors: List[PassportElementError])

object SetPassportDataErrors {
  import io.circe.generic.auto._

  implicit val method: Method[SetPassportDataErrors, Boolean] =
    new Method[SetPassportDataErrors, Boolean] {

      def name: String = "setPassportDataErrors"

      def encoder: Encoder[SetPassportDataErrors] = deriveEncoder[SetPassportDataErrors].snakeCase

      def decoder: Decoder[Boolean] = Decoder.decodeBoolean

      def uploads(request: SetPassportDataErrors): List[(String, InputFile)] = Nil
    }
}
