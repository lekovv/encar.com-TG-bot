import _root_.config.ConfigApp
import zio._
import zio.http.netty.NettyConfig
import zio.http.netty.NettyConfig.LeakDetectionLevel
import zio.http.{Client, Server}

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

  private val runtime = Scope.default

  private val base = ConfigApp.live

  private val client = Client.default

  val all =
    runtime >+>
      base >+>
      client >+>
      server
}
