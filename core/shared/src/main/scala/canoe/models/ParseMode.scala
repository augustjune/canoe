package canoe.models

import io.circe.Encoder

/**
  * Telegram supported formatting options.
  */
object ParseMode extends Enumeration {
  type ParseMode = Value
  val MarkdownV2: ParseMode = Value("MarkdownV2")
  val Markdown: ParseMode = Value("Markdown")
  val HTML: ParseMode = Value("HTML")

  implicit val parseModeEncoder: Encoder[ParseMode] =
    Encoder[String].contramap[ParseMode](_.toString)
}
