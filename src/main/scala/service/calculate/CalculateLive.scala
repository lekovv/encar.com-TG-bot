package service.calculate

import models.CarPrice
import service.exchage.ExchangeRateAPI
import service.parser.Parser
import zio.{Task, ZIO, ZLayer}

final case class CalculateLive(parser: Parser, exchange: ExchangeRateAPI) extends Calculate {

  private val margin = 0.15
  override def calculate(url: String): Task[CarPrice] = {
    for {
      data <- parser.parseHTML(url)
      won = data.price
      rateKrwToUsd <- exchange.convert("KRW", "USD")
      usd = won * rateKrwToUsd
      rateUsdToRub <- exchange.convert("USD", "RUB")
      rub = usd.toLong * rateUsdToRub
      info = CarPrice(
        data.image,
        data.model,
        data.mileage + " км",
        data.capacity + " л",
        data.prodYear,
        price = f"${(rub * (1 + margin)).toLong}%,d" + " ₽",
        desc = "test description"
      )
    } yield info
  }
}

object CalculateLive {
  val layer = ZLayer.fromZIO {
    for {
      parser   <- ZIO.service[Parser]
      exchange <- ZIO.service[ExchangeRateAPI]
    } yield CalculateLive(parser, exchange)
  }
}
