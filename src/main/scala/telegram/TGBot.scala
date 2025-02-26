package telegram

import zio.macros.accessible
import zio.{Task, ZLayer}

@accessible
trait TGBot {

  def run: Task[Unit]
}

object TGBot {
  val live: ZLayer[TGBotClient, Nothing, TGBotLive] = TGBotLive.layer
}
