import sbt._

object Dependencies {
  
  val fs2Version = "1.0.+"
  val akkaVersion = "2.5.+"
  val scalaTestVersion = "3.0.+"
  val scalaCheckVersion = "1.14.+"

  lazy val scalaTest: ModuleID = "org.scalatest" %% "scalatest" % scalaTestVersion
  lazy val scalaCheck: ModuleID = "org.scalacheck" %% "scalacheck" % scalaCheckVersion
  lazy val testDependencies: Seq[ModuleID] = Seq(scalaTest, scalaCheck).map(_ % Test)

  lazy val fs2Io: ModuleID = "co.fs2" %% "fs2-io" % fs2Version
  lazy val fs2Core: ModuleID = "co.fs2" %% "fs2-core" % fs2Version
  lazy val fs2ReactiveStreams: ModuleID = "co.fs2" %% "fs2-reactive-streams" % fs2Version withSources() withJavadoc()
  lazy val fs2Dependencies: Seq[ModuleID] = Seq(fs2Core, fs2Io, fs2ReactiveStreams) ++ testDependencies
  
  lazy val akkaStream: ModuleID = "com.typesafe.akka" %% "akka-stream" % akkaVersion
  lazy val akkaStreamDependencies: Seq[ModuleID] = Seq(akkaStream) ++ testDependencies
}
