package samples

import canoe.methods.messages.EditMessageCaption

object Run extends App {

  val method = EditMessageCaption.method
  val action = EditMessageCaption.direct(-1, 12, Some("cool stuff"))

  println(
    method
      .encoder(action)
      .asObject
      .map(
        _.toIterable
          .filterNot(kv => kv._2.isNull || kv._2.isObject)
          .map {
            case (k, j) if j.isString => k -> j.asString.get
            case (k, j)               => k -> j.toString
          }
          .toMap
      )
  )

}
