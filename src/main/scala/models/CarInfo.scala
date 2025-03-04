package models

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class CarInfo(
    image: String,
    model: String,
    mileage: Int,
    capacity: Double,
    prodYear: String,
    price: Long
)

object CarInfo {
  implicit val codec: Codec[CarInfo] = deriveCodec[CarInfo]
}
