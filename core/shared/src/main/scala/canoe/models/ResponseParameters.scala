package canoe.models

import cats.syntax.functor._
import io.circe.Decoder
import io.circe.generic.semiauto

/**
  * Contains information about why a request was unsuccessful.
  */
sealed trait ResponseParameters extends Product

object ResponseParameters {

  implicit val updateDecoder: Decoder[ResponseParameters] =
    List[Decoder[ResponseParameters]](
      semiauto.deriveDecoder[ChatMigration].widen,
      semiauto.deriveDecoder[ExceededFloodControl].widen
    ).reduceLeft(_.or(_))
}

/**
  * The group has been migrated to a supergroup with the specified identifier.
  */
final case class ChatMigration(migrateToChatId: Long) extends ResponseParameters

/**
  * Flood control was exceeded.
  *
  * @param retryAfter The number of seconds left to wait before the request can be repeated
  */
final case class ExceededFloodControl(retryAfter: Int) extends ResponseParameters
