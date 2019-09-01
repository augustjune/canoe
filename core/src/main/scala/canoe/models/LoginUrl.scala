package canoe.models

final case class LoginUrl(url: String,
                          forwardText: Option[String] = None,
                          botUsername: Option[String] = None,
                          requestWriteAccess: Option[Boolean] = None)
