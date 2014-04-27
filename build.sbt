name := "ScalaWit"
 
version := "0.1"
 
scalaVersion := "2.10.4"

resolvers += "Typesafe Repo" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.1.4" % "test",
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.0",
  "com.typesafe.play" %% "play-json" % "2.2.2",
  "com.github.nscala-time" %% "nscala-time" % "1.0.0"
)
