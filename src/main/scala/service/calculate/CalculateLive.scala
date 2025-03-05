package service.calculate

import models.CarPrice
import service.exchage.ExchangeRateAPI
import service.parser.Parser
import zio.{Task, ZIO, ZLayer}

import java.time.LocalDate
import java.time.format.DateTimeFormatter

final case class CalculateLive(parser: Parser, exchange: ExchangeRateAPI) extends Calculate {

  private val margin  = 0.15
  private val utilFee = "в личное пользование (включен в таможенные расходы)"
  private val util    = 5200
  private val addExp  = 120000
  private val desc =
    """
      |*Стоимость данного автомобиля указана под ключ до Владивостока без скрытых платежей и комиссий
      |Доставка автомобиля в ваш регион ~180-250 т. руб.
      |Более подробно можно уточнить у нашего менеджера
    """.stripMargin

  private val phone = "📱: +7 (XXX) XXX-XX-XX"
  private val addInfo =
    """                   
      |💻 developed by Vladimir Lekov
      |✉️ telegram: @lekovv
    """.stripMargin

  private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

  override def calculate(url: String): Task[CarPrice] = {
    for {
      data <- parser.parseHTML(url)
      won = data.price
      rateKrwToUsd <- exchange.convert("KRW", "USD")
      usd = won * rateKrwToUsd
      rateUsdToRub <- exchange.convert("USD", "RUB")
      rub               = ((usd.toLong * rateUsdToRub) * (1 + margin)).toLong
      capacity          = data.capacity
      prodYear          = data.prodYear
      prodYearLocalDate = LocalDate.parse(prodYear, formatter)
      customExp = (capacity, prodYearLocalDate.isBefore(LocalDate.now().minusYears(3))) match {
        case (cap, true) if cap >= 2  => 1400000 + util
        case (cap, true) if cap < 2   => 500000 + util
        case (cap, false) if cap < 2  => 1000000 + util
        case (cap, false) if cap >= 2 => 2000000 + util
      }
      total = rub + customExp + addExp
      info = CarPrice(
        data.image,
        data.model,
        data.mileage + " км",
        data.capacity + " л",
        data.prodYear,
        price = f"$rub%,d" + " ₽",
        customExp = f"$customExp%,d" + " ₽",
        utilFee = utilFee,
        addExp = f"$addExp%,d" + " ₽",
        total = f"$total%,d" + " ₽",
        desc = desc,
        phone = phone,
        addInfo = addInfo
      )
    } yield info
  }
}

object CalculateLive {
  val layer = ZLayer.fromZIO {
    for {
      parser   <- ZIO.service[Parser]
      exchange <- ZIO.service[ExchangeRateAPI]
    } yield CalculateLive(parser, exchange)
  }
}
