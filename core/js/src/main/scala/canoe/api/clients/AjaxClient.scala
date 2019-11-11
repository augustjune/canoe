package canoe.api.clients

import canoe.api.{FailedMethod, ResponseDecodingError, TelegramClient}
import canoe.methods.Method
import canoe.models.Response
import cats.effect.{Async, ContextShift}
import cats.syntax.all._
import io.circe.Decoder
import io.circe.parser.decode
import org.scalajs.dom.console
import org.scalajs.dom.ext.Ajax

private[api] class AjaxClient[F[_]: Async: ContextShift](token: String) extends TelegramClient[F] {

  private val botApiUri: String = s"https://api.telegram.org/bot$token"

  /**
    * Transforms request into result using implicit method definition as a contract.
    */
  def execute[Req, Res](request: Req)(implicit M: Method[Req, Res]): F[Res] = {
    implicit val responseDecoder: Decoder[Response[Res]] = Response.decoder[Res](M.decoder)

    sendJsonRequest(request, M).map(decode[Response[Res]]).flatMap {
      case Left(error)     => handleUnknownEntity(M.name, request, error.getMessage)
      case Right(response) => handleTelegramResponse(M, request)(response)
    }
  }

  private def handleUnknownEntity[I, A](method: String, input: I, error: String): F[A] = {
    console.error(
      s"Received unknown Telegram entity during execution of '$method' method. \nInput data: $input. \n${error}"
    )
    ResponseDecodingError(error.toString).raiseError[F, A]
  }

  private def handleTelegramResponse[A, I, C](m: Method[I, A], input: I)(response: Response[A]): F[A] =
    response match {
      case Response(true, Some(result), _, _, _) => result.pure[F]

      case failed =>
        console.error(s"Received failed response from Telegram: $failed. Method name: ${m.name}, input data: $input")
        FailedMethod(m, input, failed).raiseError[F, A]
    }

  private def sendJsonRequest[Req, Res](request: Req, method: Method[Req, Res]): F[String] = {
    val url = s"$botApiUri/${method.name}"
    val json = method.encoder.apply(request).toString

    Async
      .fromFuture(Async[F].delay(Ajax.post(url, json, headers = Map("Content-Type" -> "application/json"))))
      .map(_.responseText)
  }
}
