package service.exchage

import zio.Task
import zio.macros.accessible

@accessible
trait ExchangeRateAPI {

  def convert(from: String, to: String): Task[BigDecimal]
}

object ExchangeRateAPI {
  val live = ExchangeRateAPILive.layer
}
