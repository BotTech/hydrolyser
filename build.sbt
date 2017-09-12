import Dependencies._

name := "hydrolyser"

organization := "nz.co.bottech"

organizationName := "BotTech"

version := "0.1"

scalaVersion := Versions.scalaVersion

libraryDependencies ++= Seq(
  cats
)
libraryDependencies ++= testDependencies
