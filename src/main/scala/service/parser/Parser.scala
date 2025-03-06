package service.parser

import models.CarInfo
import zio.{Task, ZLayer}
import zio.macros.accessible

@accessible
trait Parser {

  def parseHTML(url: String): Task[CarInfo]
}

object Parser {
  val live: ZLayer[Any, Nothing, ParserLive] = ParserLive.layer
}
