package canoe.models

import canoe.models.Currency.Currency

/**
  * Information about an incoming pre-checkout query.
  *
  * @param id                Unique query identifier
  * @param from              User who sent the query
  * @param currency          Three-letter ISO 4217 currency code
  * @param totalAmount       Total price in the smallest units of the currency (integer, not float/double).
  *                          For example, for a price of US$ 1.45 pass amount = 145.
  *                          See the exp parameter in currencies.json, it shows the number of digits past the
  *                          decimal point for each currency (2 for the majority of currencies).
  * @param invoicePayload    Bot specified invoice payload
  * @param shippingOptionId  Identifier of the shipping option chosen by the user
  * @param orderInfo         Order info provided by the user
  */
case class PreCheckoutQuery(id: String,
                            from: User,
                            currency: Currency,
                            totalAmount: Long,
                            invoicePayload: String,
                            shippingOptionId: Option[String],
                            orderInfo: Option[OrderInfo])
