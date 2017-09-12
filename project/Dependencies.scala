import sbt._

object Dependencies {

  object Versions {

    val scalaVersion = "2.12.2"

    val catsVersion = "1.0.0-MF"
    // For Cats version compatibility see https://github.com/IronCoreLabs/cats-scalatest#setup
    val catsScalaTestVersion = "2.3.0"
    val scalaCheckVersion = "1.13.4"
    val scalaTestVersion = "3.0.3"
  }

  import Versions._

  val cats = "org.typelevel" %% "cats-core" % catsVersion
  val catsScalaTest = "com.ironcorelabs" %% "cats-scalatest" % catsScalaTestVersion % Test
  val scalaCheck = "org.scalacheck" %% "scalacheck" % scalaCheckVersion % Test
  val scalaTest = "org.scalatest" %% "scalatest" % scalaTestVersion % Test

  val testDependencies = Seq(
    catsScalaTest,
    scalaTest,
    scalaCheck
  )
}
