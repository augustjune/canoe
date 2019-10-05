package canoe.models

/**
  * @param name             User name
  * @param phoneNumber      User's phone number
  * @param email            User email
  * @param shippingAddress  User shipping address
  */
final case class OrderInfo(name: String, phoneNumber: String, email: String, shippingAddress: ShippingAddress)
