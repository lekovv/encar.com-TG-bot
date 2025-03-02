package service.parser
import models.CarInfo
import org.jsoup.Jsoup
import zio.{Task, ZIO, ZLayer}

final case class ParserLive() extends Parser {

  //TODO: скорее всего, скрипты загружаются динамически. использовать Selenium???
  override def parseHTML(url: String): Task[CarInfo] = ZIO.attempt {

    val html = Jsoup.connect(url).get()

    val scripts = html.getElementsByTag("script")

    println(scripts)

    val description = html
      .select("meta[property=og:description]")
      .attr("content")

    val image = html
      .select("meta[property=og:image]")
      .attr("content")

    val mileage = description
      .split(", ")(1)
      .replace("주행거리:", "")
      .trim
      .replace("km", " км")

    val yearFull = description.split(", ")(0)
    val year = yearFull
      .split(" ")(0)
      .replaceAll("[^0-9]", "")
    val month = yearFull
      .split(" ")(1)
      .replaceAll("[^0-9]", "")

    val prodYear = s"$month.$year"

    val model = "test model"

    CarInfo(image, model, mileage, prodYear)
  }
}

object ParserLive {
  val layer = ZLayer.fromFunction(ParserLive.apply _)
}
