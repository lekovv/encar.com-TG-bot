package models

import zio.json.{DeriveJsonCodec, JsonCodec}

case class CarInfo(image: String, model: String, mileage: String, year: String)

object CarInfo {
  implicit val codec: JsonCodec[CarInfo] = DeriveJsonCodec.gen
}
