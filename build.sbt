lazy val canoe = project
  .in(file("."))
  .aggregate(core, examples)
  .settings(
    projectSettings,
    skip.in(publish) := true
  )

lazy val core = project
  .settings(
    name := "canoe",
    projectSettings,
    compilerOptions,
    typeSystemEnhancements,
    dependencies,
    tests
  )

lazy val examples = project
  .dependsOn(core)
  .settings(
    name := "canoe-examples",
    skip.in(publish) := true
  )

lazy val projectSettings = Seq(
  organization := "org.augustjune",
  licenses ++= Seq(("MIT", url("http://opensource.org/licenses/MIT"))),
  homepage := Some(url("https://github.com/augustjune/canoe")),
  developers := List(
    Developer("augustjune", "Yura Slinkin", "jurij.jurich@gmail.com", url("https://github.com/augustjune"))
  ),
  scalaVersion := "2.12.8",
  crossScalaVersions := Seq(scalaVersion.value)
)

val fs2Version = "2.0.0"
val catsCoreVersion = "2.0.0"
val catsEffectVersion = "2.0.0"
val circeVersion = "0.11.1"
val http4sVersion = "0.20.10"
val scalatestVersion = "3.0.8"
val kindProjectorVersion = "0.10.3"

lazy val dependencies =
  libraryDependencies ++= Seq(
    "co.fs2" %% "fs2-core" % fs2Version,
    "org.typelevel" %% "cats-core" % catsCoreVersion,
    "org.typelevel" %% "cats-effect" % catsEffectVersion,
    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion,
    "org.http4s" %% "http4s-blaze-client" % http4sVersion,
    "org.http4s" %% "http4s-circe" % http4sVersion
  )

lazy val compilerOptions =
  scalacOptions ++= Seq(
    "-Xfatal-warnings", // Fail the compilation if there are any warnings.
    "-Ypartial-unification", // Enable partial unification in type constructor inference
    "-deprecation", // Emit warning and location for usages of deprecated APIs.
    "-explaintypes", // Explain type errors in more detail.
    "-feature", // Emit warning and location for usages of features that should be imported explicitly.
    "-language:higherKinds", // Allow higher-kinded types
    "-language:postfixOps", // Allow higher-kinded types
    "-language:implicitConversions" // Allow definition of implicit functions called views
  )

resolvers += Resolver.sonatypeRepo("releases")

lazy val typeSystemEnhancements =
  addCompilerPlugin("org.typelevel" %% "kind-projector" % kindProjectorVersion)

lazy val tests = {
  val dependencies =
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % scalatestVersion
    ).map(_ % Test)

  val frameworks =
    testFrameworks := Seq(TestFrameworks.ScalaTest)

  Seq(dependencies, frameworks)
}
