package telegram

import zio.{Task, ZIO, ZLayer}

final case class TGBotLive(bot: TGBotClient) extends TGBot {

  override def run: Task[Unit] =
    for {
      _ <- bot.startPolling()
    } yield ()
}

object TGBotLive {
  val layer: ZLayer[TGBotClient, Nothing, TGBotLive] = ZLayer.fromZIO {
    for {
      client <- ZIO.service[TGBotClient]
    } yield TGBotLive(client)
  }
}
