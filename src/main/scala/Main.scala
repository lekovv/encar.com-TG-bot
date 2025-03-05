import telegram.TGBot
import zio.Console.printLine
import zio._
import zio.logging.backend.SLF4J

object Main extends ZIOAppDefault {
  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] = Runtime.removeDefaultLoggers >>> SLF4J.slf4j

  private val program =
    for {
      _   <- ZIO.logInfo("Bot is running")
      bot <- ZIO.serviceWithZIO[TGBot](_.run).exitCode
    } yield bot

  override def run: ZIO[Any, Nothing, ExitCode] =
    program
      .provide(Layers.all)
      .foldZIO(
        err => printLine(s"Execution failed with: $err").exitCode,
        _ => ZIO.succeed(ExitCode.success)
      )
}
