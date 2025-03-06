package service.exchage

import config.ConfigApp
import zio.{Task, ZLayer}
import zio.macros.accessible

@accessible
trait ExchangeRateAPI {

  def convert(from: String, to: String): Task[BigDecimal]
}

object ExchangeRateAPI {
  val live: ZLayer[ConfigApp, Nothing, ExchangeRateAPILive] = ExchangeRateAPILive.layer
}
