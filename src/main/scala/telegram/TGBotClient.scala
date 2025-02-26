package telegram

import com.bot4s.telegram.api.declarative.{Callbacks, Commands}
import com.bot4s.telegram.cats.{Polling, TelegramBot}
import config.ConfigApp
import org.asynchttpclient.Dsl.asyncHttpClient
import sttp.client3.asynchttpclient.zio.AsyncHttpClientZioBackend
import util.Secret.SecretOps
import zio.interop.catz._
import zio.{Task, ZIO, ZLayer}

case class TGBotClient(token: String) extends TelegramBot[Task](
      token,
      AsyncHttpClientZioBackend.usingClient(zio.Runtime.default, asyncHttpClient())
    )
    with Polling[Task]
    with Commands[Task]
    with Callbacks[Task]

object TGBotClient {
  val live: ZLayer[ConfigApp, Nothing, TGBotClient] = ZLayer.fromZIO {
    for {
      config <- ZIO.service[ConfigApp]
      token = config.telegram.token.secretToString
    } yield TGBotClient(token)
  }
}
