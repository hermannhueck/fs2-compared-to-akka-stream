ThisBuild / version      := "0.1.0"
ThisBuild / scalaVersion := "2.13.0-M5"
ThisBuild / organization := "io.hueck"
ThisBuild / organizationName := "Hueck"


val fs2Version = "1.0.4"
val akkaVersion = "2.5.21"
val scalaTestVersion = "3.0.7"

lazy val fs2Io = "co.fs2" %% "fs2-io" % fs2Version
lazy val fs2Core = "co.fs2" %% "fs2-core" % fs2Version
lazy val akkaStream = "com.typesafe.akka" %% "akka-stream" % akkaVersion
lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.7"


lazy val root: Project = (project in file("."))
  .aggregate(fs2, akkastream)
  .dependsOn(fs2, akkastream)
  //.enablePlugins(JavaAppPackaging)
  .settings(
    name := "fs2-compared-to-akka-stream",
  )

lazy val fs2: Project = (project in file("fs2"))
  .settings(
    name := "fs2",
    libraryDependencies ++= Seq(fs2Core, fs2Io),
    libraryDependencies += scalaTest % Test,
  )

lazy val akkastream: Project = (project in file("akkastream"))
  .settings(
    name := "akkastream",
    libraryDependencies ++= Seq(akkaStream),
    libraryDependencies += scalaTest % Test,
  )