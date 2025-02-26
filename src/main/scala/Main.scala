import zio.Console.printLine
import zio.Runtime.setConfigProvider
import zio._
import zio.config.typesafe.TypesafeConfigProvider
import zio.logging.backend.SLF4J

object Main extends ZIOAppDefault {
  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    Runtime.removeDefaultLoggers >>> SLF4J.slf4j >>>
      setConfigProvider(
        TypesafeConfigProvider
          .fromResourcePath()
      )

  private val program =
    for {
      _ <- ZIO.logInfo("Server is running")
    } yield ()

  override def run =
    program
      .provide(Layers.all)
      .foldZIO(
        err => printLine(s"Execution failed with: $err").exitCode,
        _ => ZIO.succeed(ExitCode.success)
      )
}
