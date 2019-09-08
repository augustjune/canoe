package canoe.syntax

import canoe.models._
import canoe.models.messages._

trait Expects {

  val any: Expect[TelegramMessage] = { case m => m }

  val textMessage: Expect[TextMessage] = { case m: TextMessage => m }

  val text: Expect[String] = textMessage.map(_.text)

  def command(name: String): Expect[TextMessage] = textMessage.startingWith(s"/$name")

  val animationMessage: Expect[AnimationMessage] = { case m: AnimationMessage => m }

  val animation: Expect[Animation] = animationMessage.map(_.animation)

  val audioMessage: Expect[AudioMessage] = { case m: AudioMessage => m }

  val audio: Expect[Audio] = audioMessage.map(_.audio)

  val documentMessage: Expect[DocumentMessage] = { case m: DocumentMessage => m }

  val document: Expect[Document] = documentMessage.map(_.document)

  val gameMessage: Expect[GameMessage] = { case m: GameMessage => m }

  val game: Expect[Game] = gameMessage.map(_.game)

  val contactMessage: Expect[ContactMessage] = { case m: ContactMessage => m }

  val contact: Expect[Contact] = contactMessage.map(_.contact)

  val locationMessage: Expect[LocationMessage] = { case m: LocationMessage => m }

  val location: Expect[Location] = locationMessage.map(_.location)

  val pollMessage: Expect[PollMessage] = { case m: PollMessage => m }

  val poll: Expect[Poll] = pollMessage.map(_.poll)

  val stickerMessage: Expect[StickerMessage] = { case m: StickerMessage => m }

  val sticker: Expect[Sticker] = stickerMessage.map(_.sticker)

  val venueMessage: Expect[VenueMessage] = { case m: VenueMessage => m }

  val venue: Expect[Venue] = venueMessage.map(_.venue)

  val videoMessage: Expect[VideoMessage] = { case m: VideoMessage => m }

  val video: Expect[Video] = videoMessage.map(_.video)

  val videoNoteMessage: Expect[VideoNoteMessage] = { case m: VideoNoteMessage => m }

  val videoNote: Expect[VideoNote] = videoNoteMessage.map(_.videoNote)

  val voiceMessage: Expect[VoiceMessage] = { case m: VoiceMessage => m }

  val voice: Expect[Voice] = voiceMessage.map(_.voice)
}
