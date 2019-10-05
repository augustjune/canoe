package canoe.models

import canoe.models.ParseMode.ParseMode

/** This object represents one result of an inline query.
  * Telegram clients currently support results of the following 19 types:
  *
  *   InlineQueryResultCachedAudio
  *   InlineQueryResultCachedDocument
  *   InlineQueryResultCachedGif
  *   InlineQueryResultCachedMpeg4Gif
  *   InlineQueryResultCachedPhoto
  *   InlineQueryResultCachedSticker
  *   InlineQueryResultCachedVideo
  *   InlineQueryResultCachedVoice
  *   InlineQueryResultArticle
  *   InlineQueryResultAudio
  *   InlineQueryResultContact
  *   InlineQueryResultDocument
  *   InlineQueryResultGif
  *   InlineQueryResultLocation
  *   InlineQueryResultMpeg4Gif
  *   InlineQueryResultPhoto
  *   InlineQueryResultVenue
  *   InlineQueryResultVideo
  *   InlineQueryResultVoice
  */
sealed trait InlineQueryResult {
  def id: String
  def `type`: String
  def replyMarkup: Option[InlineKeyboardMarkup]
}

/**
  * Link to an article or web page.
  *
  * @param type                 Type of the result, must be article
  * @param id                   Unique identifier for this result, 1-64 Bytes
  * @param title                Title of the result
  * @param inputMessageContent  Content of the message to be sent
  * @param replyMarkup          Inline keyboard attached to the message
  * @param url                  URL of the result
  * @param hideUrl              Pass True, if you don't want the URL to be shown in the message
  * @param description          Short description of the result
  * @param thumbUrl             Url of the thumbnail for the result
  * @param thumbWidth           Thumbnail width
  * @param thumbHeight          Thumbnail height
  */
final case class InlineQueryResultArticle(id: String,
                                          title: String,
                                          inputMessageContent: InputMessageContent,
                                          replyMarkup: Option[InlineKeyboardMarkup] = None,
                                          url: Option[String] = None,
                                          hideUrl: Option[Boolean] = None,
                                          description: Option[String] = None,
                                          thumbUrl: Option[String] = None,
                                          thumbWidth: Option[Int] = None,
                                          thumbHeight: Option[Int] = None,
                                          `type`: String = "article")
    extends InlineQueryResult

/**
  * Link to a photo.
  *
  * By default, this photo will be sent by the user with optional caption.
  * Alternatively, you can use input_message_content to send a message with the specified content instead of the photo.
  *
  * @param type                Type of the result, must be photo
  * @param id                  Unique identifier for this result, 1-64 bytes
  * @param photoUrl            A valid URL of the photo. Photo must be in jpeg format. Photo size must not exceed 5MB
  * @param thumbUrl            URL of the thumbnail for the photo
  * @param photoWidth          Width of the photo
  * @param photoHeight         Height of the photo
  * @param title               Title for the result
  * @param description         Short description of the result
  * @param caption             Caption of the photo to be sent, 0-200 characters
  * @param parseMode           Parse mode of captured text (Markdown or HTML)
  * @param replyMarkup         Inline keyboard attached to the message
  * @param inputMessageContent Content of the message to be sent instead of the photo
  */
final case class InlineQueryResultPhoto(id: String,
                                        photoUrl: String,
                                        thumbUrl: String,
                                        photoWidth: Option[Int] = None,
                                        photoHeight: Option[Int] = None,
                                        title: Option[String] = None,
                                        description: Option[String] = None,
                                        caption: Option[String] = None,
                                        parseMode: Option[ParseMode] = None,
                                        replyMarkup: Option[InlineKeyboardMarkup] = None,
                                        inputMessageContent: Option[InputMessageContent] = None,
                                        `type`: String = "photo")
    extends InlineQueryResult

/**
  * Game.
  *
  * Note:
  * This will only work in Telegram versions released after October 1, 2016.
  * Older clients will not display any inline results if a game result is among them.
  *
  * @param type          Type of the result, must be game
  * @param id            Unique identifier for this result, 1-64 bytes
  * @param gameShortName Short name of the game
  * @param replyMarkup   Inline keyboard attached to the message
  *
  */
final case class InlineQueryResultGame(id: String,
                                       gameShortName: String,
                                       replyMarkup: Option[InlineKeyboardMarkup] = None,
                                       `type`: String = "game")
    extends InlineQueryResult

/**
  * Link to an animated GIF file.
  *
  * By default, this animated GIF file will be sent by the user with optional caption.
  * Alternatively, you can use input_message_content to send a message with the specified content instead of the animation.
  *
  * @param type                Type of the result, must be gif
  * @param id                  Unique identifier for this result, 1-64 bytes
  * @param gifUrl              A valid URL for the GIF file. File size must not exceed 1MB
  * @param gifWidth            Width of the GIF
  * @param gifHeight           Height of the GIF
  * @param gifDuration         Duration of the GIF
  * @param thumbUrl            URL of the static thumbnail for the result (jpeg or gif)
  * @param title               Title for the result
  * @param caption             Caption of the GIF file to be sent, 0-200 characters
  * @param parseMode           Parse mode of captured text (Markdown or HTML)
  * @param replyMarkup         Inline keyboard attached to the message
  * @param inputMessageContent Content of the message to be sent instead of the GIF animation
  */
final case class InlineQueryResultGif(id: String,
                                      gifUrl: String,
                                      gifWidth: Option[Int] = None,
                                      gifHeight: Option[Int] = None,
                                      gifDuration: Option[Int] = None,
                                      thumbUrl: String,
                                      title: Option[String] = None,
                                      caption: Option[String] = None,
                                      parseMode: Option[ParseMode] = None,
                                      replyMarkup: Option[InlineKeyboardMarkup] = None,
                                      inputMessageContent: Option[InputMessageContent] = None,
                                      `type`: String = "gif")
    extends InlineQueryResult

/**
  * Link to a video animation (H.264/MPEG-4 AVC video without sound).
  *
  * By default, this animated MPEG-4 file will be sent by the user with optional caption.
  * Alternatively, you can use input_message_content to send a message with the specified content instead of the animation.
  *
  * @param type                Type of the result, must be mpeg4_gif
  * @param id                  Unique identifier for this result, 1-64 bytes
  * @param mpeg4Url            A valid URL for the MP4 file. File size must not exceed 1MB
  * @param mpeg4Width          Video width
  * @param mpeg4Height         Video height
  * @param mpeg4Duration       Video duration
  * @param thumbUrl            URL of the static thumbnail (jpeg or gif) for the result
  * @param title               Title for the result
  * @param caption             Caption of the MPEG-4 file to be sent, 0-200 characters
  * @param parseMode           Parse mode of captured text (Markdown or HTML)
  * @param replyMarkup         Inline keyboard attached to the message
  * @param inputMessageContent Content of the message to be sent instead of the video animation
  */
final case class InlineQueryResultMpeg4Gif(id: String,
                                           mpeg4Url: String,
                                           mpeg4Width: Option[Int] = None,
                                           mpeg4Height: Option[Int] = None,
                                           mpeg4Duration: Option[Int] = None,
                                           thumbUrl: String,
                                           title: Option[String] = None,
                                           caption: Option[String] = None,
                                           parseMode: Option[ParseMode] = None,
                                           replyMarkup: Option[InlineKeyboardMarkup] = None,
                                           inputMessageContent: Option[InputMessageContent] = None,
                                           `type`: String = "mpeg4_gif")
    extends InlineQueryResult

/**
  * Link to a page containing an embedded video player or a video file.
  *
  * By default, this video file will be sent by the user with an optional caption.
  * Alternatively, you can use input_message_content to send a message with the specified content instead of the video.
  *
  * @param type                Type of the result, must be video
  * @param id                  Unique identifier for this result, 1-64 bytes
  * @param videoUrl            A valid URL for the embedded video player or video file
  * @param mimeType            Mime type of the content of video url, "text/html" or "video/mp4"
  * @param thumbUrl            URL of the thumbnail (jpeg only) for the video
  * @param title               Title for the result
  * @param caption             Caption of the video to be sent, 0-200 characters
  * @param parseMode           Parse mode of captured text (Markdown or HTML)
  * @param videoWidth          Video width
  * @param videoHeight         Video height
  * @param videoDuration       Video duration in seconds
  * @param description         Short description of the result
  * @param replyMarkup         Inline keyboard attached to the message
  * @param inputMessageContent Content of the message to be sent instead of the video
  */
final case class InlineQueryResultVideo(id: String,
                                        videoUrl: String,
                                        mimeType: String,
                                        thumbUrl: String,
                                        title: Option[String] = None,
                                        caption: Option[String] = None,
                                        parseMode: Option[ParseMode] = None,
                                        videoWidth: Option[Int] = None,
                                        videoHeight: Option[Int] = None,
                                        videoDuration: Option[Int] = None,
                                        description: Option[String] = None,
                                        replyMarkup: Option[InlineKeyboardMarkup] = None,
                                        inputMessageContent: Option[InputMessageContent] = None,
                                        `type`: String = "video")
    extends InlineQueryResult

/** Represents a link to an mp3 audio file. By default, this audio file will be sent by the user.
  *
  * Alternatively, you can use input_message_content to send a message with the specified content instead of the audio.
  *
  * @param type                 String Type of the result, must be audio
  * @param id                   String Unique identifier for this result, 1-64 bytes
  * @param audioUrl             String A valid URL for the audio file
  * @param title                String Title
  * @param caption              String Optional. Caption, 0-200 characters
  * @param parseMode            String Optional Send Markdown or HTML, if you want Telegram apps to show bold, italic,
  *                             fixed-width text or inline URLs in the media caption.
  * @param performer            String Optional Performer
  * @param audioDuration        Integer Optional Audio duration in seconds
  * @param replyMarkup          InlineKeyboardMarkup Optional Inline keyboard attached to the message
  * @param inputMessageContent  InputMessageContent Optional Content of the message to be sent instead of the audio
  *
  */
final case class InlineQueryResultAudio(id: String,
                                        audioUrl: String,
                                        title: String,
                                        caption: Option[String] = None,
                                        parseMode: Option[ParseMode] = None,
                                        performer: Option[String] = None,
                                        audioDuration: Option[Int] = None,
                                        replyMarkup: Option[InlineKeyboardMarkup] = None,
                                        inputMessageContent: Option[InputMessageContent] = None,
                                        `type`: String = "audio")
    extends InlineQueryResult

/**
  * Link to a voice recording in an .ogg container encoded with OPUS.
  *
  * By default, this voice recording will be sent by the user.
  * Alternatively, you can use input_message_content to send a message with the specified content instead of the the voice message.
  *
  * Note: This will only work in Telegram versions released after 9 April, 2016. Older clients will ignore them.
  *
  * @param type                Type of the result, must be voice
  * @param id                  Unique identifier for this result, 1-64 bytes
  * @param voiceUrl            A valid URL for the voice recording
  * @param title               Recording title
  * @param caption             Caption, 0-200 characters
  * @param parseMode           Parse mode of captured text (Markdown or HTML)
  * @param voiceDuration       Recording duration in seconds
  * @param replyMarkup         Inline keyboard attached to the message
  * @param inputMessageContent Content of the message to be sent instead of the voice recording
  */
final case class InlineQueryResultVoice(id: String,
                                        voiceUrl: String,
                                        title: String,
                                        caption: Option[String] = None,
                                        parseMode: Option[ParseMode] = None,
                                        voiceDuration: Option[Int] = None,
                                        replyMarkup: Option[InlineKeyboardMarkup] = None,
                                        inputMessageContent: Option[InputMessageContent] = None,
                                        `type`: String = "voice")
    extends InlineQueryResult

/**
  * Link to a file.
  *
  * By default, this file will be sent by the user with an optional caption.
  * Alternatively, you can use input_message_content to send a message with the specified content instead of the file.
  * Currently, only .PDF and .ZIP files can be sent using this method.
  *
  * @param type                Type of the result, must be document
  * @param id                  Unique identifier for this result, 1-64 bytes
  * @param title               Title for the result
  * @param caption             Caption of the document to be sent, 0-200 characters
  * @param parseMode           Parse mode of captured text (Markdown or HTML)
  * @param documentUrl         A valid URL for the file
  * @param mimeType            Mime type of the content of the file, either "application/pdf" or "application/zip"
  * @param description         Short description of the result
  * @param replyMarkup         Inline keyboard attached to the message
  * @param inputMessageContent Content of the message to be sent instead of the file
  * @param thumbUrl            URL of the thumbnail (jpeg only) for the file
  * @param thumbWidth          Thumbnail width
  * @param thumbHeight         Thumbnail height
  */
final case class InlineQueryResultDocument(id: String,
                                           title: String,
                                           caption: Option[String] = None,
                                           parseMode: Option[ParseMode] = None,
                                           documentUrl: Option[String] = None,
                                           mimeType: String, // either "application/pdf" or "application/zip"
                                           description: Option[String] = None,
                                           replyMarkup: Option[InlineKeyboardMarkup] = None,
                                           inputMessageContent: Option[InputMessageContent] = None,
                                           thumbUrl: Option[String] = None,
                                           thumbWidth: Option[Int] = None,
                                           thumbHeight: Option[Int] = None,
                                           `type`: String = "document")
    extends InlineQueryResult

/**
  * Location on a map.
  *
  * By default, the location will be sent by the user.
  * Alternatively, you can use input_message_content to send a message with the specified content instead of the location.
  *
  * Note: This will only work in Telegram versions released after 9 April, 2016. Older clients will ignore them.
  *
  * @param type                Type of the result, must be location
  * @param id                  Unique identifier for this result, 1-64 Bytes
  * @param latitude            Location latitude in degrees
  * @param longitude           Location longitude in degrees
  * @param title               Location title
  * @param livePeriod          Period in seconds for which the location can be updated, should be between 60 and 86400.
  * @param replyMarkup         Inline keyboard attached to the message
  * @param inputMessageContent Content of the message to be sent instead of the location
  * @param thumbUrl            Url of the thumbnail for the result
  * @param thumbWidth          Thumbnail width
  * @param thumbHeight         Thumbnail height
  */
final case class InlineQueryResultLocation(id: String,
                                           latitude: Double,
                                           longitude: Double,
                                           title: String,
                                           livePeriod: Option[Int] = None,
                                           replyMarkup: Option[InlineKeyboardMarkup] = None,
                                           inputMessageContent: Option[InputMessageContent] = None,
                                           thumbUrl: Option[String] = None,
                                           thumbWidth: Option[Int] = None,
                                           thumbHeight: Option[Int] = None,
                                           `type`: String = "location")
    extends InlineQueryResult

/**
  * Venue.
  *
  * By default, the venue will be sent by the user.
  * Alternatively, you can use input_message_content to send a message with the specified content instead of the venue.
  *
  * Note: This will only work in Telegram versions released after 9 April, 2016. Older clients will ignore them.
  *
  * @param type                Type of the result, must be venue
  * @param id                  Unique identifier for this result, 1-64 Bytes
  * @param latitude            Latitude of the venue location in degrees
  * @param longitude           Longitude of the venue location in degrees
  * @param title               Title of the venue
  * @param address             Address of the venue
  * @param foursquareId        Foursquare identifier of the venue if known
  * @param foursquareType      Foursquare type of the venue, if known.
  *                            For example, “arts_entertainment/default”, “arts_entertainment/aquarium” or “food/icecream”.
  * @param replyMarkup         Inline keyboard attached to the message
  * @param inputMessageContent Content of the message to be sent instead of the venue
  * @param thumbUrl            Url of the thumbnail for the result
  * @param thumbWidth          Thumbnail width
  * @param thumbHeight         Thumbnail height
  */
final case class InlineQueryResultVenue(id: String,
                                        latitude: Double,
                                        longitude: Double,
                                        title: String,
                                        address: String,
                                        foursquareId: Option[String] = None,
                                        foursquareType: Option[String] = None,
                                        replyMarkup: Option[InlineKeyboardMarkup] = None,
                                        inputMessageContent: Option[InputMessageContent] = None,
                                        thumbUrl: Option[String] = None,
                                        thumbWidth: Option[Int] = None,
                                        thumbHeight: Option[Int] = None,
                                        `type`: String = "venue")
    extends InlineQueryResult

/**
  * Contact with a phone number.
  *
  * By default, this contact will be sent by the user.
  * Alternatively, you can use input_message_content to send a message with the specified content instead of the contact.
  *
  * Note: This will only work in Telegram versions released after 9 April, 2016. Older clients will ignore them.
  *
  * @param type                Type of the result, must be contact
  * @param id                  Unique identifier for this result, 1-64 Bytes
  * @param phoneNumber         Contact's phone number
  * @param firstName           Contact's first name
  * @param lastName            Contact's last name
  * @param vcard               Additional data about the contact in the form of a vCard, 0-2048 bytes
  * @param replyMarkup         Inline keyboard attached to the message
  * @param inputMessageContent Content of the message to be sent instead of the contact
  * @param thumbUrl            Url of the thumbnail for the result
  * @param thumbWidth          Thumbnail width
  * @param thumbHeight         Thumbnail height
  */
final case class InlineQueryResultContact(id: String,
                                          phoneNumber: String,
                                          firstName: String,
                                          lastName: Option[String] = None,
                                          vcard: Option[String] = None,
                                          replyMarkup: Option[InlineKeyboardMarkup] = None,
                                          inputMessageContent: Option[InputMessageContent] = None,
                                          thumbUrl: Option[String] = None,
                                          thumbWidth: Option[Int] = None,
                                          thumbHeight: Option[Int] = None,
                                          `type`: String = "contact")
    extends InlineQueryResult

/**
  * Link to a photo stored on the Telegram servers.
  *
  * By default, this photo will be sent by the user with an optional caption.
  * Alternatively, you can use input_message_content to send a message with the specified content instead of the photo.
  *
  * @param type                Type of the result, must be photo
  * @param id                  Unique identifier for this result, 1-64 bytes
  * @param photoFileId         A valid file identifier of the photo
  * @param title               Title for the result
  * @param description         Short description of the result
  * @param caption             Caption of the photo to be sent, 0-200 characters
  * @param parseMode           Parse mode of captured text (Markdown or HTML)
  * @param replyMarkup         Inline keyboard attached to the message
  * @param inputMessageContent Content of the message to be sent instead of the photo
  */
final case class InlineQueryResultCachedPhoto(id: String,
                                              photoFileId: String,
                                              title: Option[String] = None,
                                              description: Option[String] = None,
                                              caption: Option[String] = None,
                                              parseMode: Option[ParseMode] = None,
                                              replyMarkup: Option[InlineKeyboardMarkup] = None,
                                              inputMessageContent: Option[InputMessageContent] = None,
                                              `type`: String = "photo")
    extends InlineQueryResult

/**
  * Link to an animated GIF file stored on the Telegram servers.
  *
  * By default, this animated GIF file will be sent by the user with an optional caption.
  * Alternatively, you can use input_message_content to send a message with specified content instead of the animation.
  *
  * @param type                Type of the result, must be gif
  * @param id                  String            Unique identifier for this result, 1-64 bytes
  * @param gifFileId           A valid file identifier for the GIF file
  * @param title               Title for the result
  * @param caption             Caption of the GIF file to be sent, 0-200 characters
  * @param parseMode           Parse mode of captured text (Markdown or HTML)
  * @param replyMarkup         An Inline keyboard attached to the message
  * @param inputMessageContent Content of the message to be sent instead of the GIF animation
  */
final case class InlineQueryResultCachedGif(id: String,
                                            gifFileId: String,
                                            title: Option[String] = None,
                                            caption: Option[String] = None,
                                            parseMode: Option[ParseMode] = None,
                                            replyMarkup: Option[InlineKeyboardMarkup] = None,
                                            inputMessageContent: Option[InputMessageContent] = None,
                                            `type`: String = "gif")
    extends InlineQueryResult

/**
  * Link to a video animation (H.264/MPEG-4 AVC video without sound) stored on the Telegram servers.
  *
  * By default, this animated MPEG-4 file will be sent by the user with an optional caption.
  * Alternatively, you can use input_message_content to send a message with the specified content instead of the animation.
  *
  * @param type                Type of the result, must be mpeg4_gif
  * @param id                  Unique identifier for this result, 1-64 bytes
  * @param mpeg4FileId         A valid file identifier for the MP4 file
  * @param title               Title for the result
  * @param caption             Caption of the MPEG-4 file to be sent, 0-200 characters
  * @param parseMode           Parse mode of captured text (Markdown or HTML)
  * @param replyMarkup         An Inline keyboard attached to the message
  * @param inputMessageContent Content of the message to be sent instead of the video animation
  */
final case class InlineQueryResultCachedMpeg4Gif(id: String,
                                                 mpeg4FileId: String,
                                                 title: Option[String] = None,
                                                 caption: Option[String] = None,
                                                 parseMode: Option[ParseMode] = None,
                                                 replyMarkup: Option[InlineKeyboardMarkup] = None,
                                                 inputMessageContent: Option[InputMessageContent] = None,
                                                 `type`: String = "mpeg4_gif")
    extends InlineQueryResult

/**
  * Link to a sticker stored on the Telegram servers.
  *
  * By default, this sticker will be sent by the user.
  * Alternatively, you can use input_message_content to send a message with the specified content instead of the sticker.
  *
  * Note: This will only work in Telegram versions released after 9 April, 2016. Older clients will ignore them.
  *
  * @param type                Type of the result, must be sticker
  * @param id                  Unique identifier for this result, 1-64 bytes
  * @param stickerFileId       A valid file identifier of the sticker
  * @param replyMarkup         An Inline keyboard attached to the message
  * @param inputMessageContent Content of the message to be sent instead of the sticker
  */
final case class InlineQueryResultCachedSticker(id: String,
                                                stickerFileId: String,
                                                replyMarkup: Option[InlineKeyboardMarkup] = None,
                                                inputMessageContent: Option[InputMessageContent] = None,
                                                `type`: String = "sticker")
    extends InlineQueryResult

/**
  * Link to a file stored on the Telegram servers.
  *
  * By default, this file will be sent by the user with an optional caption.
  * Alternatively, you can use input_message_content to send a message with the specified content instead of the file.
  * Currently, only pdf-files and zip archives can be sent using this method.
  *
  * Note: This will only work in Telegram versions released after 9 April, 2016. Older clients will ignore them.
  *
  * @param type                Type of the result, must be document
  * @param id                  Unique identifier for this result, 1-64 bytes
  * @param title               Title for the result
  * @param documentFileId      A valid file identifier for the file
  * @param description         Short description of the result
  * @param caption             Caption of the document to be sent, 0-200 characters
  * @param parseMode           Parse mode of captured text (Markdown or HTML)
  * @param replyMarkup         An Inline keyboard attached to the message
  * @param inputMessageContent Content of the message to be sent instead of the file
  */
final case class InlineQueryResultCachedDocument(id: String,
                                                 title: String,
                                                 documentFileId: String,
                                                 description: Option[String] = None,
                                                 caption: Option[String] = None,
                                                 parseMode: Option[ParseMode] = None,
                                                 replyMarkup: Option[InlineKeyboardMarkup] = None,
                                                 inputMessageContent: Option[InputMessageContent] = None,
                                                 `type`: String = "document")
    extends InlineQueryResult

/**
  * Link to a video file stored on the Telegram servers.
  *
  * By default, this video file will be sent by the user with an optional caption.
  * Alternatively, you can use input_message_content to send a message with the specified content instead of the video.
  *
  * @param type                Type of the result, must be video
  * @param id                  Unique identifier for this result, 1-64 bytes
  * @param videoFileId         A valid file identifier for the video file
  * @param title               Title for the result
  * @param description         Short description of the result
  * @param caption             Caption of the video to be sent, 0-200 characters
  * @param parseMode           Parse mode of captured text (Markdown or HTML)
  * @param replyMarkup         An Inline keyboard attached to the message
  * @param inputMessageContent Content of the message to be sent instead of the video
  */
final case class InlineQueryResultCachedVideo(id: String,
                                              videoFileId: String,
                                              title: String,
                                              description: Option[String] = None,
                                              caption: Option[String] = None,
                                              parseMode: Option[ParseMode] = None,
                                              replyMarkup: Option[InlineKeyboardMarkup] = None,
                                              inputMessageContent: Option[InputMessageContent] = None,
                                              `type`: String = "video")
    extends InlineQueryResult

/**
  * Link to a voice message stored on the Telegram servers.
  *
  * By default, this voice message will be sent by the user.
  * Alternatively, you can use input_message_content to send a message with the specified content instead of the voice message.
  *
  * Note: This will only work in Telegram versions released after 9 April, 2016. Older clients will ignore them.
  *
  * @param type                Type of the result, must be voice
  * @param id                  Unique identifier for this result, 1-64 bytes
  * @param voiceFileId         A valid file identifier for the voice message
  * @param title               Voice message title
  * @param caption             Caption, 0-200 characters
  * @param parseMode           Parse mode of captured text (Markdown or HTML)
  * @param replyMarkup         An Inline keyboard attached to the message
  * @param inputMessageContent Content of the message to be sent instead of the voice message
  */
final case class InlineQueryResultCachedVoice(id: String,
                                              voiceFileId: String,
                                              title: String,
                                              caption: Option[String] = None,
                                              parseMode: Option[ParseMode] = None,
                                              replyMarkup: Option[InlineKeyboardMarkup] = None,
                                              inputMessageContent: Option[InputMessageContent] = None,
                                              `type`: String = "voice")
    extends InlineQueryResult

/**
  * Link to an mp3 audio file stored on the Telegram servers.
  *
  * By default, this audio file will be sent by the user.
  * Alternatively, you can use input_message_content to send a message with the specified content instead of the audio.
  *
  * Note: This will only work in Telegram versions released after 9 April, 2016. Older clients will ignore them.
  *
  * @param type                Type of the result, must be audio
  * @param id                  Unique identifier for this result, 1-64 bytes
  * @param audioFileId         A valid file identifier for the audio file
  * @param caption             Caption, 0-200 characters
  * @param parseMode           Parse mode of captured text (Markdown or HTML)
  * @param replyMarkup         An Inline keyboard attached to the message
  * @param inputMessageContent Content of the message to be sent instead of the audio
  */
final case class InlineQueryResultCachedAudio(id: String,
                                              audioFileId: String,
                                              caption: Option[String] = None,
                                              parseMode: Option[ParseMode] = None,
                                              replyMarkup: Option[InlineKeyboardMarkup] = None,
                                              inputMessageContent: Option[InputMessageContent] = None,
                                              `type`: String = "audio")
    extends InlineQueryResult
