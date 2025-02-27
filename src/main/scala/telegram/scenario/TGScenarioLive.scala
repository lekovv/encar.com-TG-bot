package telegram.scenario

import cats.implicits.toFunctorOps
import com.bot4s.telegram.models.{InlineKeyboardButton, InlineKeyboardMarkup}
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

    bot.onMessage { implicit msg =>
      val text = msg.text.getOrElse("")
      if (text.startsWith("https://fem.encar.com/")) {
        val cancelButton = InlineKeyboardButton.callbackData("Отменить", "cancel")
        val markup       = InlineKeyboardMarkup.singleColumn(Seq(cancelButton))

        bot.replyMd("Идет просчет стоимости, пожалуйста, ожидайте...", replyMarkup = Option(markup)).void
      } else if (text.nonEmpty && text != "/start") {
        bot.reply("Пожалуйста, отправьте корректную ссылку с сайта encar.com").void
      } else ZIO.unit
    }
  }
}

object TGScenarioLive {
  val layer = ZLayer.fromFunction(TGScenarioLive.apply _)
}
