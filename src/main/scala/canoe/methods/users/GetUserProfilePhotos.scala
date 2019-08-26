package canoe.methods.users

import canoe.marshalling.{CirceDecoders, CirceEncoders}
import canoe.methods.{JsonRequest, Method}
import canoe.models.{InputFile, UserProfilePhotos}
import io.circe.{Decoder, Encoder}

/** Use this method to get a list of profile pictures for a user. Returns a UserProfilePhotos object.
  *
  * @param userId Integer Unique identifier of the target user
  * @param offset Integer Optional Sequential number of the first photo to be returned. By default, all photos are returned.
  * @param limit  Integer Optional Limits the number of photos to be retrieved. Values between 1-100 are accepted. Defaults to 100.
  */
case class GetUserProfilePhotos(userId: Int,
                                offset: Option[Int] = None,
                                limit: Option[Int] = None
                               ) extends JsonRequest[UserProfilePhotos]

object GetUserProfilePhotos {

  implicit val method: Method[GetUserProfilePhotos, UserProfilePhotos] =
    new Method[GetUserProfilePhotos, UserProfilePhotos] {

      def name: String = "getUserProfilePhotos"

      def encoder: Encoder[GetUserProfilePhotos] = CirceEncoders.getUserProfilePhotosEncoder

      def decoder: Decoder[UserProfilePhotos] = CirceDecoders.userProfilePhotosDecoder

      def uploads(request: GetUserProfilePhotos): List[(String, InputFile)] = Nil
    }
}
