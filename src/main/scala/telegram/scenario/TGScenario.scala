package telegram.scenario

import com.bot4s.telegram.api.BotBase
import zio.Task
import zio.macros.accessible

@accessible
trait TGScenario[Bot <: BotBase[Task]] {

  def listen(bot: Bot): Task[Unit]
}

object TGScenario {
  val live = TGScenarioLive.layer
}
