package canoe.api.models

import canoe.models._

/**
  * A wrapper around the value of Option[ReplyMarkup].
  */
trait Keyboard {
  def replyMarkup: Option[ReplyMarkup]
}

object Keyboard {

  /**
    * Custom keyboard which can substitute user default keyboard.
    * Pressing any of the button will send the message to the chat.
    */
  case class Reply(markup: ReplyKeyboardMarkup) extends Keyboard {
    def replyMarkup: Option[ReplyMarkup] = Some(markup)
  }

  /**
    * Custom keyboard, which buttons are attached to the message.
    */
  case class Inline(markup: InlineKeyboardMarkup) extends Keyboard {
    def replyMarkup: Option[ReplyMarkup] = Some(markup)
  }

  /**
    * Leave the current keyboard (or its absence) unchanged.
    */
  case object Unchanged extends Keyboard {
    def replyMarkup: Option[ReplyMarkup] = None
  }

  /**
    * Clear current reply keyboard.
    */
  case object Remove extends Keyboard {
    def replyMarkup: Option[ReplyMarkup] = Some(ReplyKeyboardRemove())
  }

  /**
    * Display reply interface to the attached message.
    * Can be extremely useful if you want to create user-friendly step-by-step interface.
    */
  case object MessageReply extends Keyboard {
    def replyMarkup: Option[ReplyMarkup] = Some(ForceReply())
  }
}
