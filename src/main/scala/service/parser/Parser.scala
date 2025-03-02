package service.parser

import models.CarInfo
import zio.Task
import zio.macros.accessible

@accessible
trait Parser {

  def parseHTML(url: String): Task[CarInfo]
}

object Parser {
  val live = ParserLive.layer
}
