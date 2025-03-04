package service.calculate

import models.CarPrice
import service.parser.Parser
import zio.{Task, ZIO, ZLayer}

final case class CalculateLive(parser: Parser) extends Calculate {

  override def calculate(url: String): Task[CarPrice] = {
    for {
      data <- parser.parseHTML(url)
      cost = CarPrice(
        data.image,
        data.model,
        data.mileage + " км",
        data.capacity + " л",
        data.prodYear,
        price = data.price + " (переведем в рубли и добавим таможенные расходы)",
        desc = "test description"
      )
    } yield cost
  }
}

object CalculateLive {
  val layer = ZLayer.fromZIO {
    for {
      parser <- ZIO.service[Parser]
    } yield CalculateLive(parser)
  }
}
