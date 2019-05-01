import Dependencies._

val projectName = "fs2-compared-to-akka-stream"
val githubId = "hermannhueck"
val githubHome = s"https://github.com/$githubId"
val projectUrl = s"$githubHome/$projectName"

inThisBuild(
  Seq(
    organization := "io.hueck",
    organizationName := "Hueck",
    description := "This project compares the streaming libraries fs2 and Akka Stream",
    homepage := Some(url(projectUrl)),
    startYear := Some(2019),
    licenses := Vector(("MIT", url("https://opensource.org/licenses/MIT"))),
    scmInfo := Some(ScmInfo(url(projectUrl), s"$projectUrl.git")),
    developers := List(
      Developer(id = githubId, name = "Hermann Hueck", email = "", url = url(githubHome))
    ),

    version := "0.1.0",
    scalaVersion := "2.13.0-M5",

    scalacOptions ++= Seq(
      "-encoding", "UTF-8", // source files are in UTF-8
      "-deprecation", // warn about use of deprecated APIs
      "-unchecked", // warn about unchecked type parameters
      "-feature", // warn about misused language features
      //"-language:higherKinds",  // suppress warnings when using higher kinded types
      //"-Ypartial-unification",  // (removed in scala 2.13) allow the compiler to unify type constructors of different arities
      //"-Xlint",                 // enable handy linter warnings
      //"-Xfatal-warnings",       // turn compiler warnings into errors
    )
  )
)

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.9")

// root project
lazy val root = (project in file("."))
  .aggregate(fs2, akkastream)
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := projectName,
  )

// project containing fs2 examples
lazy val fs2 = (project in file("fs2"))
  .aggregate(util)
  .dependsOn(util)
  .settings(
    name := "fs2",
    description := "examples implemented with fs2",
    libraryDependencies ++= fs2Dependencies // defined in project/Dependencies.scala
  )

// project containing akka-stream examples
lazy val akkastream = (project in file("akkastream"))
  .aggregate(util)
  .dependsOn(util)
  .settings(
    name := "akkastream",
    description := "examples implemented with akka-stream",
    libraryDependencies ++= akkaStreamDependencies, // defined in project/Dependencies.scala
  )

// utilities project (used by fs2 and akkastream)
lazy val util = (project in file("util"))
  .settings(
    name := "util",
    description := "utilities",
  )


addCommandAlias("frm", "fs2/runMain")
addCommandAlias("arm", "akkastream/runMain")
