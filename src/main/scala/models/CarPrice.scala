package models

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

case class CarPrice(
    image: String,
    model: String,
    mileage: String,
    capacity: String,
    prodYear: String,
    price: String,
    customExp: String,
    utilFee: String,
    addExp: String,
    total: String,
    desc: String,
    phone: String,
    addInfo: String
)

object CarPrice {
  implicit val codec: Codec[CarInfo] = deriveCodec[CarInfo]
}
