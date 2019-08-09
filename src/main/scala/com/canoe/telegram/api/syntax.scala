package com.canoe.telegram.api

import com.canoe.telegram.models.{Animation, Audio, Contact, Document, InputFile, Location, PhotoSize, Sticker, Venue, Video, VideoNote, Voice}
import com.canoe.telegram.models.outgoing.{AnimationContent, AudioContent, ContactContent, DocumentContent, LocationContent, PhotoContent, StickerContent, TextContent, VenueContent, VideoContent, VideoNoteContent, VoiceContent}

object syntax {

  implicit def textContent(text: String): TextContent =
    TextContent(text)

  implicit def locationMessageContent(location: Location): LocationContent =
    LocationContent(location.latitude, location.longitude)

  implicit def animationMessageContent(animation: Animation): AnimationContent =
    AnimationContent(InputFile.Existing(animation.fileId))

  implicit def audioMessageContent(audio: Audio): AudioContent =
    AudioContent(InputFile.Existing(audio.fileId))

  implicit def contactMessageContent(contact: Contact): ContactContent =
    ContactContent(contact.phoneNumber, contact.firstName, contact.lastName, contact.vcard)

  implicit def documentMessageContent(document: Document): DocumentContent =
    DocumentContent(InputFile.Existing(document.fileId))

  implicit def photoMessageContent(photo: PhotoSize): PhotoContent =
    PhotoContent(InputFile.Existing(photo.fileId))

  implicit def stickerMessageContent(sticker: Sticker): StickerContent =
    StickerContent(InputFile.Existing(sticker.fileId))

  implicit def videoMessageContent(video: Video): VideoContent =
    VideoContent(InputFile.Existing(video.fileId))

  implicit def videoNoteMessageContent(videoNote: VideoNote): VideoNoteContent =
    VideoNoteContent(InputFile.Existing(videoNote.fileId))

  implicit def venueMessageContent(venue: Venue): VenueContent =
    VenueContent(venue.location.longitude, venue.location.latitude, venue.title, venue.address)

  implicit def voiceMessageContent(voice: Voice): VoiceContent =
    VoiceContent(InputFile.Existing(voice.fileId))

}
