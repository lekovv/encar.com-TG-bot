package service.parser
import exception.Exceptions._
import io.circe.Json
import io.circe.parser._
import models.CarInfo
import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.RemoteWebDriver
import zio.{Task, ZIO, ZLayer}

import java.net.URL
import java.time.Duration

final case class ParserLive() extends Parser {

  override def parseHTML(url: String): Task[CarInfo] = ZIO.attempt {

    val gridUrl = new URL("http://selenium-standalone-chromium:4444")

    val options = new ChromeOptions()
    options.addArguments("--headless")
    options.addArguments("--no-sandbox")
    options.addArguments("--disable-extensions")
    options.addArguments("--disable-infobars")
    options.addArguments("--disable-gpu")
    options.addArguments("--disable-dev-shm-usage")

    val driver: RemoteWebDriver = new RemoteWebDriver(gridUrl, options)

    try {
      driver.manage().timeouts().implicitlyWait(Duration.ofMillis(3000))
      driver.get(url)

      val script = driver
        .findElement(By.xpath("//script[contains(text(), 'PRELOADED_STATE')]"))
        .getAttribute("innerHTML")

      val json = script
        .replaceAll("__PRELOADED_STATE__ = ", "")
        .trim

      val body = parse(json) match {
        case Right(value) => value
        case Left(err)    => throw ParsingException(s"failed to parse body: ${err.getMessage}")
      }

      val image = body.hcursor
        .downField("cars")
        .downField("base")
        .downField("photos")
        .as[List[Json]] match {
        case Right(photos) =>
          photos.find(
            _.hcursor
              .downField("code")
              .as[String]
              .getOrElse("") == "001"
          ) match {
            case Some(value) =>
              val path = value.hcursor
                .downField("path")
                .as[String]
                .getOrElse("")
              s"https://ci.encar.com/carpicture${path}"
            case None => throw PhotoNotFoundException("photo with code 001 not found")
          }
        case Left(err) => throw ParsingException(s"failed to get image: ${err.getMessage}")
      }

      val manufacturerEnglishName = body.hcursor
        .downField("cars")
        .downField("base")
        .downField("category")
        .downField("manufacturerEnglishName")
        .as[String] match {
        case Right(value) => value
        case Left(err)    => throw ParsingException(s"failed to get manufacturerEnglishName: ${err.getMessage}")
      }

      val modelGroupEnglishName = body.hcursor
        .downField("cars")
        .downField("base")
        .downField("category")
        .downField("modelGroupEnglishName")
        .as[String] match {
        case Right(value) => value
        case Left(err)    => throw ParsingException(s"failed to get modelGroupEnglishName: ${err.getMessage}")
      }

      val gradeEnglishName = body.hcursor
        .downField("cars")
        .downField("base")
        .downField("category")
        .downField("gradeEnglishName")
        .as[String] match {
        case Right(value) => value
        case Left(err)    => throw ParsingException(s"failed to get gradeEnglishName: ${err.getMessage}")
      }

      val mileage = body.hcursor
        .downField("cars")
        .downField("base")
        .downField("spec")
        .downField("mileage")
        .as[Int] match {
        case Right(value) => value
        case Left(err)    => throw ParsingException(s"failed to get mileage: ${err.getMessage}")
      }

      val displacement = body.hcursor
        .downField("cars")
        .downField("base")
        .downField("spec")
        .downField("displacement")
        .as[Int] match {
        case Right(value) => value.toString
        case Left(err)    => throw ParsingException(s"failed to get displacement: ${err.getMessage}")
      }

      val engineCapacity = s"${displacement.charAt(0)}.${displacement.substring(1)}".toDouble

      val priceWon = body.hcursor
        .downField("cars")
        .downField("base")
        .downField("advertisement")
        .downField("price")
        .as[Long] match {
        case Right(value) => value * 10000
        case Left(err)    => throw ParsingException(s"failed to get price: ${err.getMessage}")
      }

      val yearMonth = body.hcursor
        .downField("cars")
        .downField("base")
        .downField("category")
        .downField("yearMonth")
        .as[String] match {
        case Right(value) => value
        case Left(err)    => throw ParsingException(s"failed to get yearMonth: ${err.getMessage}")
      }

      val year  = yearMonth.substring(0, 4)
      val month = yearMonth.substring(4, 6)

      CarInfo(
        image = s"$image",
        model = s"$manufacturerEnglishName $modelGroupEnglishName $gradeEnglishName",
        mileage = mileage,
        capacity = engineCapacity,
        prodYear = s"01.$month.$year",
        price = priceWon
      )

    } finally {
      driver.quit()
    }
  }
}

object ParserLive {
  val layer: ZLayer[Any, Nothing, ParserLive] = ZLayer.fromFunction(ParserLive.apply _)
}
