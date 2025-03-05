import _root_.config.ConfigApp
import service.calculate.Calculate
import service.exchage.ExchangeRateAPI
import service.parser.Parser
import telegram.scenario.TGScenario
import telegram.{TGBot, TGBotClient}
import zio.Scope

object Layers {

  private val runtime = Scope.default
  private val base    = ConfigApp.live

  val all =
    base >+>
      runtime >+>
      TGBotClient.live >+>
      Parser.live >+>
      ExchangeRateAPI.live >+>
      Calculate.live >+>
      TGScenario.live >+>
      TGBot.live
}
