name := "canoe"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0-M1"
libraryDependencies += "org.typelevel" %% "cats-free" % "2.0.0-M1"
libraryDependencies += "org.typelevel" %% "cats-effect" % "1.3.0"

libraryDependencies += "co.fs2" %% "fs2-core" % "1.0.4"

val circeVersion = "0.10.0"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)
