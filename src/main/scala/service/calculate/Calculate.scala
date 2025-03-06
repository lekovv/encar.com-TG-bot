package service.calculate

import models.{CarInfo, CarPrice}
import service.exchage.ExchangeRateAPI
import service.parser.Parser
import zio.{Task, ZLayer}
import zio.macros.accessible

@accessible
trait Calculate {

  def calculate(url: String): Task[CarPrice]
}

object Calculate {
  val live: ZLayer[ExchangeRateAPI with Parser, Nothing, CalculateLive] = CalculateLive.layer
}
