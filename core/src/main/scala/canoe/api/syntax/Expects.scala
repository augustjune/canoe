package canoe.api.syntax

import canoe.models._
import canoe.models.messages._

trait Expects {

  val any: ExpectMessage[TelegramMessage] = { case m => m }

  val textMessage: ExpectMessage[TextMessage] = { case m: TextMessage => m }

  val text: ExpectMessage[String] = textMessage andThen (_.text)

  def command(name: String): ExpectMessage[TextMessage] = textMessage.startsWith(s"/$name")

  val animationMessage: ExpectMessage[AnimationMessage] = { case m: AnimationMessage => m }

  val animation: ExpectMessage[Animation] = animationMessage andThen (_.animation)

  val audioMessage: ExpectMessage[AudioMessage] = { case m: AudioMessage => m }

  val audio: ExpectMessage[Audio] = audioMessage andThen (_.audio)

  val documentMessage: ExpectMessage[DocumentMessage] = { case m: DocumentMessage => m }

  val document: ExpectMessage[Document] = documentMessage andThen (_.document)

  val gameMessage: ExpectMessage[GameMessage] = { case m: GameMessage => m }

  val game: ExpectMessage[Game] = gameMessage andThen (_.game)

  val contactMessage: ExpectMessage[ContactMessage] = { case m: ContactMessage => m }

  val contact: ExpectMessage[Contact] = contactMessage andThen (_.contact)

  val locationMessage: ExpectMessage[LocationMessage] = { case m: LocationMessage => m }

  val location: ExpectMessage[Location] = locationMessage andThen (_.location)

  val stickerMessage: ExpectMessage[StickerMessage] = { case m: StickerMessage => m }

  val sticker: ExpectMessage[Sticker] = stickerMessage andThen (_.sticker)

  val venueMessage: ExpectMessage[VenueMessage] = { case m: VenueMessage => m }

  val venue: ExpectMessage[Venue] = venueMessage andThen (_.venue)

  val videoMessage: ExpectMessage[VideoMessage] = { case m: VideoMessage => m }

  val video: ExpectMessage[Video] = videoMessage andThen (_.video)

  val videoNoteMessage: ExpectMessage[VideoNoteMessage] = { case m: VideoNoteMessage => m }

  val videoNote: ExpectMessage[VideoNote] = videoNoteMessage andThen (_.videoNote)

  val voiceMessage: ExpectMessage[VoiceMessage] = { case m: VoiceMessage => m }

  val voice: ExpectMessage[Voice] = voiceMessage andThen (_.voice)
}
