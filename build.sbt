name := "canoe"

version := "0.1"

scalaVersion := "2.12.8"

scalacOptions ++= Seq(
  "-feature",
  "-language:higherKinds",             // Allow higher-kinded types
  "-language:postfixOps",             // Allow higher-kinded types
  "-language:implicitConversions",     // Allow definition of implicit functions called views
  "-Xfatal-warnings",
  "-Ypartial-unification"
)

resolvers += Resolver.sonatypeRepo("releases")

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3")

libraryDependencies += "org.typelevel" %% "cats-core" % "2.0.0-M1"
libraryDependencies += "org.typelevel" %% "cats-free" % "2.0.0-M1"
libraryDependencies += "org.typelevel" %% "cats-effect" % "1.3.0"

libraryDependencies += "co.fs2" %% "fs2-core" % "1.0.4"

libraryDependencies += "com.softwaremill.sttp" %% "core" % "1.5.19"
libraryDependencies += "com.softwaremill.sttp" %% "async-http-client-backend-cats" % "1.5.19"

libraryDependencies += "com.typesafe" % "config" % "1.3.4"

val circeVersion = "0.10.0"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-generic-extras",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)
