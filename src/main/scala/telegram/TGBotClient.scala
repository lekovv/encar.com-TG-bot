package telegram

import _root_.config.ConfigApp
import com.bot4s.telegram.api.declarative._
import com.bot4s.telegram.cats._
import org.asynchttpclient.Dsl.asyncHttpClient
import sttp.client3.asynchttpclient.zio.AsyncHttpClientZioBackend
import util.Secret.SecretOps
import zio._
import zio.interop.catz._

case class TGBotClient(token: String) extends TelegramBot[Task](
      token,
      AsyncHttpClientZioBackend.usingClient(zio.Runtime.default, asyncHttpClient())
    )
    with Polling[Task]
    with Commands[Task]
    with RegexCommands[Task]
    with Callbacks[Task]

object TGBotClient {
  val live: ZLayer[ConfigApp, Nothing, TGBotClient] = ZLayer.fromZIO {
    for {
      config <- ZIO.service[ConfigApp]
      token = config.telegram.token.secretToString
    } yield TGBotClient(token)
  }
}
