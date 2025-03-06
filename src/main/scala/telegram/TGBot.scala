package telegram

import config.ConfigApp
import telegram.scenario.TGScenario
import zio.{RIO, ZLayer}
import zio.macros.accessible

@accessible
trait TGBot {

  def run: RIO[ConfigApp, Unit]
}

object TGBot {
  val live: ZLayer[TGScenario[TGBotClient] with TGBotClient, Nothing, TGBotLive] = TGBotLive.layer
}
