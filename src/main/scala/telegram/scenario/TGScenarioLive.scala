package telegram.scenario

import cats.implicits.toFunctorOps
import telegram.TGBotClient
import zio.interop.catz.asyncInstance
import zio.{Task, ZIO, ZLayer}

final case class TGScenarioLive() extends TGScenario[TGBotClient] {

  override def listen(bot: TGBotClient): Task[Unit] = ZIO.attempt {

    bot.onRegex("""^/start$""".r) { implicit msg =>
      { _ =>
        bot.reply("Отправьте ссылку на автомобиль с сайта encar.com").void
      }
    }
  }
}

object TGScenarioLive {
  val layer = ZLayer.fromFunction(TGScenarioLive.apply _)
}
