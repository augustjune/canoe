package canoe.api

import canoe.models._
import canoe.models.outgoing._

object syntax {

  implicit def textContent(text: String): TextContent =
    TextContent(text)

  implicit def locationMessageContent(location: Location): LocationContent =
    LocationContent(location.latitude, location.longitude)

  implicit def animationMessageContent(animation: Animation): AnimationContent =
    AnimationContent(InputFile.fromFileId(animation.fileId))

  implicit def audioMessageContent(audio: Audio): AudioContent =
    AudioContent(InputFile.fromFileId(audio.fileId))

  implicit def contactMessageContent(contact: Contact): ContactContent =
    ContactContent(contact.phoneNumber, contact.firstName, contact.lastName, contact.vcard)

  implicit def documentMessageContent(document: Document): DocumentContent =
    DocumentContent(InputFile.fromFileId(document.fileId))

  implicit def photoMessageContent(photo: PhotoSize): PhotoContent =
    PhotoContent(InputFile.fromFileId(photo.fileId))

  implicit def stickerMessageContent(sticker: Sticker): StickerContent =
    StickerContent(InputFile.fromFileId(sticker.fileId))

  implicit def videoMessageContent(video: Video): VideoContent =
    VideoContent(InputFile.fromFileId(video.fileId))

  implicit def videoNoteMessageContent(videoNote: VideoNote): VideoNoteContent =
    VideoNoteContent(InputFile.fromFileId(videoNote.fileId))

  implicit def venueMessageContent(venue: Venue): VenueContent =
    VenueContent(venue.location.longitude, venue.location.latitude, venue.title, venue.address)

  implicit def voiceMessageContent(voice: Voice): VoiceContent =
    VoiceContent(InputFile.fromFileId(voice.fileId))

}
