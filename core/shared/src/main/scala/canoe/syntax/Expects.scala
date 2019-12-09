package canoe.syntax

import canoe.models._
import canoe.models.messages._

trait Expects {

  /**
    * Partial function which matches any incoming messages
    */
  val any: Expect[TelegramMessage] = { case m => m }

  /**
    * Partial function which matches only text messages
    */
  val textMessage: Expect[TextMessage] = { case m: TextMessage => m }

  /**
    * Partial function which matches only text messages and returns contained text
    */
  val text: Expect[String] = textMessage.andThen(_.text)

  /**
    * Partial function which matches only text messages starting with a command with provided name
    */
  def command(name: String): Expect[TextMessage] = textMessage.startingWith(s"/$name")

  /**
    * Partial function which matches only animation messages
    */
  val animationMessage: Expect[AnimationMessage] = { case m: AnimationMessage => m }

  /**
    * Partial function which matches only animation messages and returns contained animation
    */
  val animation: Expect[Animation] = animationMessage.andThen(_.animation)

  /**
    * Partial function which matches only audio messages
    */
  val audioMessage: Expect[AudioMessage] = { case m: AudioMessage => m }

  /**
    * Partial function which matches only audio messages and returns contained audio
    */
  val audio: Expect[Audio] = audioMessage.andThen(_.audio)

  /**
    * Partial function which matches only document messages
    */
  val documentMessage: Expect[DocumentMessage] = { case m: DocumentMessage => m }

  /**
    * Partial function which matches only document messages and returns contained document
    */
  val document: Expect[Document] = documentMessage.andThen(_.document)

  /**
    * Partial function which matches only game messages
    */
  val gameMessage: Expect[GameMessage] = { case m: GameMessage => m }

  /**
    * Partial function which matches only game messages and returns contained game
    */
  val game: Expect[Game] = gameMessage.andThen(_.game)

  /**
    * Partial function which matches only contact messages
    */
  val contactMessage: Expect[ContactMessage] = { case m: ContactMessage => m }

  /**
    * Partial function which matches only contact messages and returns contained contact
    */
  val contact: Expect[Contact] = contactMessage.andThen(_.contact)

  /**
    * Partial function which matches only location messages
    */
  val locationMessage: Expect[LocationMessage] = { case m: LocationMessage => m }

  /**
    * Partial function which matches only location messages and returns contained location
    */
  val location: Expect[Location] = locationMessage.andThen(_.location)

  /**
    * Partial function which matches only photo messages
    */
  val photoMessage: Expect[PhotoMessage] = { case m: PhotoMessage => m }

  /**
    * Partial function which matches only photo messages and returns contained photo
    */
  val photo: Expect[PhotoSize] = photoMessage.andThen(_.photo.last)

  /**
    * Partial function which matches only poll messages
    */
  val pollMessage: Expect[PollMessage] = { case m: PollMessage => m }

  /**
    * Partial function which matches only poll messages and returns contained poll
    */
  val poll: Expect[Poll] = pollMessage.andThen(_.poll)

  /**
    * Partial function which matches only sticker messages
    */
  val stickerMessage: Expect[StickerMessage] = { case m: StickerMessage => m }

  /**
    * Partial function which matches only sticker messages and returns contained sticker
    */
  val sticker: Expect[Sticker] = stickerMessage.andThen(_.sticker)

  /**
    * Partial function which matches only venue messages
    */
  val venueMessage: Expect[VenueMessage] = { case m: VenueMessage => m }

  /**
    * Partial function which matches only venue messages and returns contained venue
    */
  val venue: Expect[Venue] = venueMessage.andThen(_.venue)

  /**
    * Partial function which matches only video messages
    */
  val videoMessage: Expect[VideoMessage] = { case m: VideoMessage => m }

  /**
    * Partial function which matches only video messages and returns contained video
    */
  val video: Expect[Video] = videoMessage.andThen(_.video)

  /**
    * Partial function which matches only video note messages
    */
  val videoNoteMessage: Expect[VideoNoteMessage] = { case m: VideoNoteMessage => m }

  /**
    * Partial function which matches only video note messages and returns contained video note
    */
  val videoNote: Expect[VideoNote] = videoNoteMessage.andThen(_.videoNote)

  /**
    * Partial function which matches only voice messages
    */
  val voiceMessage: Expect[VoiceMessage] = { case m: VoiceMessage => m }

  /**
    * Partial function which matches only voice messages and returns contained voice
    */
  val voice: Expect[Voice] = voiceMessage.andThen(_.voice)
}
