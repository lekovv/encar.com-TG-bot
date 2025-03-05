import sbt.*

object Dependencies {

  object Version {
    val scala        = "2.13.10"
    val zio          = "2.1.15"
    val zioConfig    = "4.0.3"
    val sl4j         = "2.0.16"
    val zioLogging   = "2.3.1"
    val logback      = "1.5.8"
    val scalaLogging = "3.9.5"
    val bot4s        = "5.7.0"
    val sttp         = "3.9.0"
    val zioCats      = "23.1.0.0"
    val catsEffect   = "3.4.8"
    val selenium     = "4.6.0"
    val circe        = "0.14.2"
  }

  object ZIO {
    lazy val core   = "dev.zio" %% "zio"              % Version.zio
    lazy val macros = "dev.zio" %% "zio-macros"       % Version.zio
    lazy val cats   = "dev.zio" %% "zio-interop-cats" % Version.zioCats
  }

  object CIRCE {
    lazy val core    = "io.circe" %% "circe-core"    % Version.circe
    lazy val generic = "io.circe" %% "circe-generic" % Version.circe
    lazy val parse   = "io.circe" %% "circe-parser"  % Version.circe
  }

  object CATS {
    lazy val catsEffect = "org.typelevel" %% "cats-effect" % Version.catsEffect
  }

  object LOGS {
    lazy val sl4j           = "org.slf4j"                   % "slf4j-api"          % Version.sl4j
    lazy val logback        = "ch.qos.logback"              % "logback-classic"    % Version.logback
    lazy val zioLogging     = "dev.zio"                    %% "zio-logging"        % Version.zioLogging
    lazy val zioLoggingLf4j = "dev.zio"                    %% "zio-logging-slf4j2" % Version.zioLogging
    lazy val scalaLogging   = "com.typesafe.scala-logging" %% "scala-logging"      % Version.scalaLogging
  }

  object HTTP {
    lazy val sttp = "com.softwaremill.sttp.client3" %% "async-http-client-backend-zio" % Version.sttp
  }

  object SELENIUM {
    lazy val core   = "org.seleniumhq.selenium" % "selenium-java"          % Version.selenium
    lazy val driver = "org.seleniumhq.selenium" % "selenium-chrome-driver" % Version.selenium
  }

  object CONFIG {
    lazy val core     = "dev.zio" %% "zio-config"          % Version.zioConfig
    lazy val magnolia = "dev.zio" %% "zio-config-magnolia" % Version.zioConfig
    lazy val typesafe = "dev.zio" %% "zio-config-typesafe" % Version.zioConfig
    lazy val refined  = "dev.zio" %% "zio-config-refined"  % Version.zioConfig
  }

  object TELEGRAM {
    lazy val bot4s = "com.bot4s" %% "telegram-core" % Version.bot4s
  }

  lazy val globalProjectDependencies = Seq(
    ZIO.core,
    ZIO.macros,
    ZIO.cats,
    CIRCE.core,
    CIRCE.generic,
    CIRCE.parse,
    CATS.catsEffect,
    LOGS.scalaLogging,
    LOGS.logback,
    LOGS.zioLoggingLf4j,
    LOGS.zioLogging,
    LOGS.sl4j,
    HTTP.sttp,
    SELENIUM.core,
    SELENIUM.driver,
    CONFIG.typesafe,
    CONFIG.refined,
    CONFIG.magnolia,
    CONFIG.core,
    TELEGRAM.bot4s
  )
}
