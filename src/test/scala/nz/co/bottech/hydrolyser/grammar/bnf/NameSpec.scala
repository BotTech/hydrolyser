package nz.co.bottech.hydrolyser.grammar.bnf

import cats.scalatest.EitherMatchers
import org.scalatest.{Matchers, WordSpec}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

class NameSpec extends WordSpec with GeneratorDrivenPropertyChecks with Matchers with EitherMatchers {

  "A parsed name" when {
    "containing a valid name" should {
      "contain the text and name from within the angle brackets" in {
        val name = "S"
        val text = s"<$name>"
        Name.parse(text) should beRight(Name(name, text))
      }
    }
    "it does not start with an angle bracket" should {
      "be invalid" in {
        val text = "S>"
        val message = s"Name '$text' does not start with '<'"
        Name.parse(text) should beLeft(PositionalError(message, text, 0))
      }
    }
    "it does not end with an angle bracket" should {
      "be invalid" in {
        val text = "<S"
        val message = s"Name '$text' does not end with '>'"
        Name.parse(text) should beLeft(PositionalError(message, text, text.length - 1))
      }
    }
    "it does not contain a name" should {
      "be invalid" in {
        val text = "<>"
        val message = s"Name '$text' is empty"
        Name.parse(text) should beLeft(PositionalError(message, text, 1))
      }
    }
    "only contains a space" should {
      "be invalid" in {
        val text = "< >"
        val message = s"Name '$text' contains only whitespace"
        Name.parse(text) should beLeft(PositionalError(message, text, 1))
      }
    }
    "starts with a space" should {
      "trim the space" in {
        val name = "S"
        val text = s"< $name>"
        Name.parse(text) should beRight(Name(name, text))
      }
    }
    "ends with a space" should {
      "trim the space" in {
        val name = "S"
        val text = s"<$name >"
        Name.parse(text) should beRight(Name(name, text))
      }
    }
    "containing only whitespace" should {
      "be invalid" in {
        forAll()
        val text = "< >"
        val message = s"Name '$text' contains only whitespace"
        Name.parse(text) should beLeft(PositionalError(message, text, 1))
      }
    }
  }
}
