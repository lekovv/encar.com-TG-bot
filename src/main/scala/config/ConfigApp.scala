package config

import zio.Config.Secret
import zio.config.magnolia.deriveConfig
import zio.config.{toKebabCase, ConfigOps}
import zio.{Config, ZIO, ZLayer}

case class Interface(
    host: String,
    port: Int
)

case class TelegramConfig(token: Secret)

case class ExchangeRateApiConfig(token: Secret)

case class ConfigApp(
    interface: Interface,
    telegram: TelegramConfig,
    exchange: ExchangeRateApiConfig
)

object ConfigApp {

  implicit val configDescriptor: Config[ConfigApp] = (
    deriveConfig[Interface].nested("interface") zip
      deriveConfig[TelegramConfig].nested("telegramConfig") zip
      deriveConfig[ExchangeRateApiConfig].nested("exchangeRateApiConfig")
  )
    .to[ConfigApp]
    .mapKey(toKebabCase)

  val live: ZLayer[Any, Config.Error, ConfigApp] = ZLayer.fromZIO {
    ZIO.config[ConfigApp](configDescriptor)
  }
}
