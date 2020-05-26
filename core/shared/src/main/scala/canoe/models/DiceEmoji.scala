package canoe.models

import cats.syntax.either._
import io.circe.{Decoder, DecodingFailure, Encoder, HCursor, Json}

sealed trait DiceEmoji

case object Dice extends DiceEmoji {
  override def toString: String = "🎲"
}

case object Darts extends DiceEmoji {
  override def toString: String = "🎯"
}

object DiceEmoji {
  implicit val decoder: Decoder[DiceEmoji] = (c: HCursor) =>
    c.as[String].flatMap {
      case e if e == Dice.toString  => Dice.asRight
      case e if e == Darts.toString => Darts.asRight
      case _              => DecodingFailure.apply("Unsupported dice emoji", List.empty).asLeft[DiceEmoji]
    }

  implicit val encoder: Encoder[DiceEmoji] = {
    case Dice  => Json.fromString(Dice.toString)
    case Darts => Json.fromString(Darts.toString)
  }
}
