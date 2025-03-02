package models

import zio.json.{DeriveJsonCodec, JsonCodec}

case class CarPrice(image: String, model: String, mileage: String, year: String, desc: String)

object CarPrice {
  implicit val codec: JsonCodec[CarPrice] = DeriveJsonCodec.gen
}
