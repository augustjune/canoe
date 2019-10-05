package canoe.methods.users

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.{InputFile, UserProfilePhotos}
import io.circe.generic.semiauto._
import io.circe.{Decoder, Encoder}

/**
  * Use this method to get a list of profile pictures for a user.
  *
  * @param userId Unique identifier of the target user
  * @param offset Sequential number of the first photo to be returned.
  *               By default, all photos are returned.
  * @param limit  Limits the number of photos to be retrieved.
  *               Values between 1-100 are accepted. Defaults to 100.
  */
final case class GetUserProfilePhotos(userId: Int, offset: Option[Int] = None, limit: Option[Int] = None)

object GetUserProfilePhotos {
  import io.circe.generic.auto._

  implicit val method: Method[GetUserProfilePhotos, UserProfilePhotos] =
    new Method[GetUserProfilePhotos, UserProfilePhotos] {

      def name: String = "getUserProfilePhotos"

      def encoder: Encoder[GetUserProfilePhotos] = deriveEncoder[GetUserProfilePhotos].snakeCase

      def decoder: Decoder[UserProfilePhotos] = deriveDecoder[UserProfilePhotos]

      def uploads(request: GetUserProfilePhotos): List[(String, InputFile)] = Nil
    }
}
