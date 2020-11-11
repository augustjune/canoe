import sbtcrossproject.CrossPlugin.autoImport.crossProject

lazy val canoe = project
  .in(file("."))
  .aggregate(coreJvm, coreJs, examples)
  .disablePlugins(MimaPlugin)
  .settings(
    projectSettings,
    crossScalaVersions := Nil,
    skip.in(publish) := true
  )

lazy val core = crossProject(JVMPlatform, JSPlatform)
  .in(file("core"))
  .settings(
    name := "canoe",
    projectSettings,
    compilerOptions,
    typeSystemEnhancements,
    crossDependencies,
    tests
  )
  .jvmSettings(
    libraryDependencies ++= Seq(
      "org.http4s"        %% "http4s-dsl"          % http4sVersion,
      "org.http4s"        %% "http4s-blaze-client" % http4sVersion,
      "org.http4s"        %% "http4s-blaze-server" % http4sVersion,
      "org.http4s"        %% "http4s-circe"        % http4sVersion,
      "io.chrisdavenport" %% "log4cats-slf4j"      % log4catsVersion
    )
  )
  .jsSettings(
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % scalaJsDomVersion
    )
  )

lazy val coreJvm = core.jvm.settings(mimaSettings)
lazy val coreJs = core.js.disablePlugins(MimaPlugin)

lazy val examples = project
  .dependsOn(coreJvm)
  .disablePlugins(MimaPlugin)
  .settings(
    name := "canoe-examples",
    skip.in(publish) := true,
    projectSettings,
    crossScalaVersions := Seq(scalaVersion.value)
  )

lazy val projectSettings = Seq(
  organization := "org.augustjune",
  licenses ++= Seq(("MIT", url("http://opensource.org/licenses/MIT"))),
  homepage := Some(url("https://github.com/augustjune/canoe")),
  developers := List(
    Developer("augustjune", "Yura Slinkin", "jurij.jurich@gmail.com", url("https://github.com/augustjune"))
  ),
  scalaVersion := scala2_13,
  crossScalaVersions := Seq(scala2_12, scalaVersion.value)
)

lazy val crossDependencies =
  libraryDependencies ++= Seq(
    "co.fs2"            %%% "fs2-core"      % fs2Version,
    "org.typelevel"     %%% "cats-core"     % catsCoreVersion,
    "org.typelevel"     %%% "cats-effect"   % catsEffectVersion,
    "io.circe"          %%% "circe-core"    % circeVersion,
    "io.circe"          %%% "circe-generic" % circeVersion,
    "io.circe"          %%% "circe-parser"  % circeVersion,
    "io.chrisdavenport" %%% "log4cats-core" % log4catsVersion
  )

lazy val mimaSettings = Seq(
  mimaPreviousArtifacts := Set(organization.value %% name.value % "0.4.0")
)

lazy val compilerOptions =
  scalacOptions ++= Seq(
    "-Xfatal-warnings", // Fail the compilation if there are any warnings.
    "-deprecation", // Emit warning and location for usages of deprecated APIs.
    "-explaintypes", // Explain type errors in more detail.
    "-feature", // Emit warning and location for usages of features that should be imported explicitly.
    "-language:higherKinds", // Allow higher-kinded types
    "-language:postfixOps", // Allow higher-kinded types
    "-language:implicitConversions", // Allow definition of implicit functions called views
    "-Ywarn-unused:implicits", // Warn if an implicit parameter is unused.
    "-Ywarn-unused:imports", // Warn if an import selector is not referenced.
    "-Ywarn-unused:locals", // Warn if a local definition is unused.
    // "-Ywarn-unused:params", // Warn if a value parameter is unused.
    "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
    "-Ywarn-unused:privates", // Warn if a private member is unused.
    "-Ywarn-value-discard" // Warn when non-Unit expression results are unused.
  ) ++ (if (scalaBinaryVersion.value.startsWith("2.12")) List("-Ypartial-unification") else Nil)

lazy val typeSystemEnhancements = Seq(
  addCompilerPlugin("org.typelevel"  %% "kind-projector"  % kindProjectorVersion),
  addCompilerPlugin("org.augustjune" %% "context-applied" % contextAppliedVersion)
)

lazy val tests = {
  val dependencies =
    libraryDependencies ++= Seq(
      "org.scalatest"              %% "scalatest"                 % scalatestVersion,
      "org.typelevel"              %% "cats-laws"                 % catsLawsVersion,
      "org.typelevel"              %% "discipline-scalatest"      % disciplineVersion,
      "com.github.alexarchambault" %% "scalacheck-shapeless_1.14" % scalacheckShapelessVersion
    ).map(_ % Test)

  val frameworks =
    testFrameworks := Seq(TestFrameworks.ScalaTest)

  Seq(dependencies, frameworks)
}

val scala2_13 = "2.13.3"
val scala2_12 = "2.12.8"

val fs2Version = "2.4.4"
val catsCoreVersion = "2.2.0"
val catsEffectVersion = "2.2.0"
val catsLawsVersion = "2.2.0"
val circeVersion = "0.13.0"
val http4sVersion = "0.21.9"
val log4catsVersion = "1.1.1"
val scalatestVersion = "3.2.2"
val disciplineVersion = "1.0.0-RC2"
val scalacheckShapelessVersion = "1.2.5"
val scalaJsDomVersion = "1.1.0"
val kindProjectorVersion = "0.10.3"
val contextAppliedVersion = "0.1.4"
