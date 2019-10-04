package canoe.models

/**
  * User's profile pictures.
  *
  * @param totalCount Total number of profile pictures the target user has
  * @param photos     Requested profile pictures (in up to 4 sizes each)
  */
case class UserProfilePhotos(totalCount: Int, photos: List[List[PhotoSize]])
