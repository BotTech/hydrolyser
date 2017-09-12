package nz.co.bottech.hydrolyser.grammar.bnf

case class Name(name: String, text: String)

object Name {

  def parse(text: String): Either[PositionalError, Name] = {
    for {
      start <- startOfNameIndex(text)
      end <- endOfNameIndex(text)
      name <- extractName(text, start, end)
    } yield {
      Name(name, text)
    }
  }

  private def startOfNameIndex(text: String): Either[PositionalError, Int] = {
    if (text.startsWith("<")) {
      Right(1)
    } else {
      Left(PositionalError(s"Name '$text' does not start with '<'", text, 0))
    }
  }

  private def endOfNameIndex(text: String): Either[PositionalError, Int] = {
    if (text.endsWith(">")) {
      Right(text.length - 1)
    } else {
      Left(PositionalError(s"Name '$text' does not end with '>'", text, text.length - 1))
    }
  }

  private def extractName(text: String, start: Int, end: Int): Either[PositionalError, String] = {
    if (end > start) {
      Right(text.substring(start, end))
    } else {
      Left(PositionalError(s"Name '$text' is empty", text, start))
    }
  }
}
