package service.parser
import models.CarInfo
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import zio.{Task, ZIO, ZLayer}

//TODO: разобраться с подключением chromedriver (403)
final case class ParserLive() extends Parser {

  override def parseHTML(url: String): Task[CarInfo] = ZIO.attempt {

    val options = new ChromeOptions()
//    options.addArguments("--headless")
    options.addArguments("--disable-extensions")

//    val service: ChromeDriverService = new ChromeDriverService.Builder().usingPort(8080).build()
    val driver: WebDriver = new ChromeDriver(options)
//    driver.manage().timeouts().implicitlyWait(Duration.ofMillis(10000))

    CarInfo(
      image = "https://ci.encar.com/carpicture/carpicture02/pic3892/38926760_001.jpg",
      model = "BMW",
      mileage = "0",
      year = "2022.11"
    )
  }
  /* скрипт: __PRELOADED_STATE__
  путь для фотографии: cars \ base \ photos(массив)
  в нем ориентироваться на поле code = 001
  к переменной path добавлять https://ci.encar.com/carpicture для формирования полной ссылки на фотографию
   */

  /*
  путь для параметров машины: cars \ base \ category
  "manufacturerName": "BMW"
  "modelGroupEnglishName": "5-Series"
  "gradeEnglishName": "523d M Sport"
   */

  /*
  путь для цены: cars \ base \ advertisement \ price (нужно умножить на 10 000)
   */

  /*
  путь для пробега: cars \ base \ spec \ mileage
   */

  /*
  найти объем двигателя
   */
}

object ParserLive {
  val layer = ZLayer.fromFunction(ParserLive.apply _)
}
