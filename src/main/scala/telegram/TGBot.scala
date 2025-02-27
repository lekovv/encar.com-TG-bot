package telegram

import config.ConfigApp
import zio.RIO
import zio.macros.accessible

@accessible
trait TGBot {

  def run: RIO[ConfigApp, Unit]
}

object TGBot {
  val live = TGBotLive.layer
}
