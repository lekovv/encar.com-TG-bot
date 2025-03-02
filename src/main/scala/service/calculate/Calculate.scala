package service.calculate

import models.{CarInfo, CarPrice}
import zio.Task
import zio.macros.accessible

@accessible
trait Calculate {

  def calculate(url: String): Task[CarPrice]
}

object Calculate {
  val live = CalculateLive.layer
}
