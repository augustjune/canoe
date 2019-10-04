package canoe.models

/**
  * @param id     Shipping option identifier
  * @param title  Option title
  * @param prices List of price portions
  */
case class ShippingOption(id: String, title: String, prices: List[LabeledPrice])
