package telegram.scenario

import cats.implicits.toFunctorOps
import com.bot4s.telegram.methods.{EditMessageText, SendMessage}
import com.bot4s.telegram.models.{ChatId, InlineKeyboardButton, InlineKeyboardMarkup}
import telegram.TGBotClient
import zio.interop.catz.asyncInstance
import zio.{Task, ZIO, ZLayer}

final case class TGScenarioLive() extends TGScenario[TGBotClient] {

  // TODO: вывести метод для расчета в отдельный файл, создать модель данных и возвращать ее
  private def calculate(url: String): Task[String] = ZIO.attempt {
    Thread.sleep(5000)
    "Стоимость автомобиля: тебе бесплатно, брат"
  }
  override def listen(bot: TGBotClient): Task[Unit] = ZIO.attempt {

    bot.onMessage { implicit msg =>
      val url = msg.text.getOrElse("")
      if (url.startsWith("https://fem.encar.com/")) {

        val cancelButton = InlineKeyboardButton.callbackData("Отменить", "cancel")
        val cancelMarkup = InlineKeyboardMarkup.singleColumn(Seq(cancelButton))

        for {
          _ <- bot
            .request(
              SendMessage(
                ChatId(msg.source),
                text = "Идет расчет стоимости, пожалуйста, ожидайте...",
                replyMarkup = Option(cancelMarkup)
              )
            )
          _ <- calculate(url)
            .foldZIO(
              err => bot.reply(s"Произошла ошибка при расчете стоимости $err").void,
              cost =>
                bot.request(
                  SendMessage(
                    ChatId(msg.source),
                    text = s"$cost"
                  )
                )
            )
            .fork
        } yield ()

      } else if (url.nonEmpty && url != "/start") {
        bot.reply("Пожалуйста, отправьте корректную ссылку с сайта encar.com").void
      } else if (url.nonEmpty && url == "/start") {
        bot.reply("Отправьте ссылку на автомобиль с сайта encar.com").void
      } else ZIO.unit
    }

    // TODO: реализовать отмену фоновой задачи расчета
    bot.onCallbackWithTag("cancel") { implicit cbq =>
      for {
        ack <- bot.ackCallback(Option(cbq.from.firstName + " отменил расчет")).fork
        response <- bot
          .request(
            EditMessageText(
              Option(ChatId(cbq.message.get.source)),
              Option(cbq.message.get.messageId),
              text = "Расчет отменен"
            )
          )
          .fork
        _ <- ack.zip(response).join
      } yield ()
    }
  }
}

object TGScenarioLive {
  val layer = ZLayer.fromFunction(TGScenarioLive.apply _)
}
