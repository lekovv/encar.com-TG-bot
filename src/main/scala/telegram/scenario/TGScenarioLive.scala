package telegram.scenario

import cats.implicits.toFunctorOps
import com.bot4s.telegram.methods.ParseMode.Markdown
import com.bot4s.telegram.methods.{EditMessageText, SendMessage, SendPhoto}
import com.bot4s.telegram.models.{ChatId, InlineKeyboardButton, InlineKeyboardMarkup, InputFile}
import models.CarPrice
import service.calculate.Calculate
import telegram.TGBotClient
import zio.interop.catz.asyncInstance
import zio.{Task, ZIO, ZLayer}

final case class TGScenarioLive(calc: Calculate) extends TGScenario[TGBotClient] {

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
          _ <- calc
            .calculate(url)
            .foldZIO(
              err => bot.reply(s"Произошла ошибка при расчете стоимости $err").void,
              { case CarPrice(image, model, mileage, capacity, prodYear, price, desc) =>
                for {
                  _ <- bot.request(
                    SendPhoto(
                      chatId = ChatId(msg.source),
                      photo = InputFile(image),
                      caption = Option(
                        s"""*Модель:* *$model*
                           |*Пробег:* *$mileage*
                           |*Объем двигателя:* *$capacity*
                           |*Год выпуска:* *$prodYear*
                           |*Стоимость автомобиля:* *$price*
                           |
                           |*Описание:* $desc
                         """.stripMargin
                      ),
                      parseMode = Option(Markdown)
                    )
                  )
                } yield ()
              }
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
  val layer = ZLayer.fromZIO {
    for {
      calc <- ZIO.service[Calculate]
    } yield TGScenarioLive(calc)
  }
}
