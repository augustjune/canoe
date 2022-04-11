package canoe.methods.updates

import canoe.marshalling.codecs._
import canoe.methods.Method
import canoe.models.UpdateType.UpdateType
import canoe.models.{InputFile, Update}
import io.circe.generic.semiauto
import io.circe.{Decoder, Encoder, Json}

/**
  * Use this method to receive incoming updates using long polling (wiki).
  * List of Update objects is returned.
  *
  * '''Notes'''
  *   1. This method will not work if an outgoing webhook is set up.
  *   2. In order to avoid getting duplicate updates, recalculate offset after each server response.
  *
  * @param offset         Identifier of the first update to be returned.
  *                       Must be greater by one than the highest among the identifiers of previously received updates.
  *                       By default, updates starting with the earliest unconfirmed update are returned.
  *                       An update is considered confirmed as soon as getUpdates is called with an offset higher than its updateId.
  *                       The negative offset can be specified to retrieve updates starting from -offset update from the end of the updates queue.
  *                       All previous updates will forgotten.
  * @param limit          Limits the number of updates to be retrieved. Values between 1-100 are accepted.
  *                       Defaults to 100.
  * @param timeout        Timeout in seconds for long polling. Defaults to 0, i.e. usual short polling
  * @param allowedUpdates List the types of updates you want your bot to receive.
  *                       Specify an empty list to receive all updates regardless of type (default).
  *                       If not specified, the previous setting will be used.
  *                       Please note that this parameter doesn't affect updates created before the call to the getUpdates,
  *                       so unwanted updates may be received for a short period of time.
  *
  */
final case class GetUpdates(offset: Option[Long] = None,
                            limit: Option[Int] = None,
                            timeout: Option[Int] = None,
                            allowedUpdates: Option[Seq[UpdateType]] = None)

object GetUpdates {
  import io.circe.generic.auto._

  implicit val method: Method[GetUpdates, List[Update]] =
    new Method[GetUpdates, List[Update]] {

      def name: String = "getUpdates"

      def encoder: Encoder[GetUpdates] =
        semiauto.deriveEncoder[GetUpdates].snakeCase

      def decoder: Decoder[List[Update]] = Decoder.decodeList

      def attachments(request: GetUpdates): List[(String, InputFile)] = Nil
    }

  /**
    * Decodes a list of updates ignoring ones which could not be decoded
    */
  val accumulativeDecoder: Decoder[List[Update]] =
    Decoder.decodeList[Json].map(_.map(Decoder[Update].decodeJson).collect { case Right(v) => v })
}
