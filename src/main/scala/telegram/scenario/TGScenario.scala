package telegram.scenario

import com.bot4s.telegram.api.BotBase
import service.calculate.Calculate
import zio.{Task, ZLayer}
import zio.macros.accessible

@accessible
trait TGScenario[Bot <: BotBase[Task]] {

  def listen(bot: Bot): Task[Unit]
}

object TGScenario {
  val live: ZLayer[Calculate, Nothing, TGScenarioLive] = TGScenarioLive.layer
}
