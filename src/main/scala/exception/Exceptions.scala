package exception

object Exceptions {

  case class ParsingException(message: String)       extends Exception
  case class PhotoNotFoundException(message: String) extends Exception
  case class HTTPException(message: String)          extends Exception

}
