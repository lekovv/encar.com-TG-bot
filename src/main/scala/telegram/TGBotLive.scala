package telegram

import config.ConfigApp
import telegram.scenario.TGScenario
import zio.{RIO, ZIO, ZLayer}

final case class TGBotLive(
    client: TGBotClient,
    listener: TGScenario[TGBotClient]
) extends TGBot {

  override def run: RIO[ConfigApp, Unit] =
    for {
      _ <- listener.listen(client)
      _ <- client.startPolling()
    } yield ()
}

object TGBotLive {
  val layer = ZLayer.fromZIO {
    for {
      client   <- ZIO.service[TGBotClient]
      listener <- ZIO.service[TGScenario[TGBotClient]]
    } yield TGBotLive(client, listener)
  }
}
