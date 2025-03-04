import _root_.config.ConfigApp
import service.calculate.Calculate
import service.exchage.ExchangeRateAPI
import service.parser.Parser
import telegram.scenario.TGScenario
import telegram.{TGBot, TGBotClient}
import zio._
import zio.http.Server
import zio.http.netty.NettyConfig
import zio.http.netty.NettyConfig.LeakDetectionLevel

object Layers {

  private val serverConf = ZLayer.fromZIO {
    ZIO.config[ConfigApp].map { config =>
      Server.Config.default.port(config.interface.port)
    }
  }

  private val nettyConf = ZLayer.succeed(
    NettyConfig.default
      .leakDetection(LeakDetectionLevel.DISABLED)
  )

  private lazy val server = (serverConf ++ nettyConf) >>> Server.customized

  private val base = ConfigApp.live

  val all =
    base >+>
      server >+>
      TGBotClient.live >+>
      Parser.live >+>
      ExchangeRateAPI.live >+>
      Calculate.live >+>
      TGScenario.live >+>
      TGBot.live
}
