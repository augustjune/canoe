lazy val root = (project in file(".")).settings(
  commonSettings,
  compilerOptions,
  typeSystemEnhancements,
  dependencies,
  tests
)

val fs2Version                 = "1.0.5"
val catsCoreVersion            = "1.6.1"
val catsEffectVersion          = "1.4.0"
val circeVersion               = "0.11.1"
val http4sVersion              = "0.20.10"
val sttpVersion                = "1.6.4"
val scalatestVersion           = "3.0.8"
val kindProjectorVersion       = "0.10.3"
val typesafeConfigVersion      = "1.3.4"

lazy val commonSettings = Seq(
  organization := "com.augustjune",
  name := "canoe",
  scalaVersion := "2.12.8",
  version := "0.0.1"
)

lazy val compilerOptions =
  scalacOptions ++= Seq(
    "-Xfatal-warnings",                  // Fail the compilation if there are any warnings.
    "-Ypartial-unification",             // Enable partial unification in type constructor inference
    "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
    "-explaintypes",                     // Explain type errors in more detail.
    "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
    "-language:higherKinds",             // Allow higher-kinded types
    "-language:postfixOps",              // Allow higher-kinded types
    "-language:implicitConversions"      // Allow definition of implicit functions called views
  )

resolvers += Resolver.sonatypeRepo("releases")

lazy val typeSystemEnhancements =
  addCompilerPlugin("org.typelevel" %% "kind-projector" % kindProjectorVersion)

lazy val dependencies =
  libraryDependencies ++= Seq(
    "co.fs2" %% "fs2-core" % fs2Version,
    "org.typelevel" %% "cats-core" % catsCoreVersion,
    "org.typelevel" %% "cats-free" % catsCoreVersion,
    "org.typelevel" %% "cats-effect" % catsEffectVersion,
    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-generic-extras" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion,
    "org.http4s" %% "http4s-blaze-client" % http4sVersion,
    "org.http4s" %% "http4s-circe" % http4sVersion,
    "com.softwaremill.sttp" %% "core" % sttpVersion,
    "com.softwaremill.sttp" %% "async-http-client-backend-cats" % sttpVersion,
    "com.typesafe" % "config" % typesafeConfigVersion
  )

lazy val tests = {
  val dependencies =
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % scalatestVersion
    ).map(_ % Test)

  val frameworks =
    testFrameworks := Seq(TestFrameworks.ScalaTest)

  Seq(dependencies, frameworks)
}
