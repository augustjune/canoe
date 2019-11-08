package canoe.models

/**
  * Information about an incoming shipping query.
  *
  * @param id               Unique query identifier
  * @param from             User who sent the query
  * @param invoicePayload   Bot specified invoice payload
  * @param shippingAddress  User specified shipping address
  */
final case class ShippingQuery(id: String, from: User, invoicePayload: String, shippingAddress: ShippingAddress)
