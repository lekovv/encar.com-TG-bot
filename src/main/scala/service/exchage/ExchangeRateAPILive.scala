package service.exchage

import config.ConfigApp
import exception.Exceptions._
import io.circe.parser._
import org.asynchttpclient.Dsl.asyncHttpClient
import sttp.client3.asynchttpclient.zio.AsyncHttpClientZioBackend
import sttp.client3.{basicRequest, UriContext}
import util.Secret.SecretOps
import zio.{Task, ZIO, ZLayer}

final case class ExchangeRateAPILive(token: String) extends ExchangeRateAPI {

  override def convert(from: String, to: String): Task[BigDecimal] = {

    val url     = s"https://v6.exchangerate-api.com/v6/$token/pair/$from/$to"
    val backend = AsyncHttpClientZioBackend.usingClient(zio.Runtime.default, asyncHttpClient())
    val request = basicRequest.get(uri"$url")

    for {
      response <- request
        .send(backend)
        .mapError(err => HTTPException(s"HTTP request failed: ${err.getMessage}"))
      body = response.body match {
        case Right(value) => value
        case Left(err)    => throw ParsingException(s"failed to get body: $err")
      }
      json = parse(body) match {
        case Right(value) => value
        case Left(err)    => throw ParsingException(s"failed to parse json: $err")
      }
      rate = json.hcursor
        .downField("conversion_rate")
        .as[BigDecimal] match {
        case Right(value) => value
        case Left(err)    => throw ParsingException(s"failed to get conversionRate: $err")
      }
    } yield rate
  }
}

object ExchangeRateAPILive {
  val layer: ZLayer[ConfigApp, Nothing, ExchangeRateAPILive] = ZLayer.fromZIO {
    for {
      config <- ZIO.service[ConfigApp]
      token = config.exchange.token.secretToString
    } yield ExchangeRateAPILive(token)
  }
}
