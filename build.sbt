import sbtcrossproject.CrossPlugin.autoImport.crossProject

lazy val canoe = project
  .in(file("."))
  .aggregate(coreJvm, coreJs, examples)
  .disablePlugins(MimaPlugin)
  .settings(
    projectSettings,
    crossScalaVersions := Nil,
    publish / skip := true
  )

lazy val core = crossProject(JVMPlatform, JSPlatform)
  .in(file("core"))
  .settings(
    name := "canoe",
    projectSettings,
    compilerOptions,
    crossDependencies,
    tests
  )
  .jvmSettings(
    libraryDependencies ++= Seq(
      "org.http4s"    %% "http4s-dsl"          % http4sVersion,
      "org.http4s"    %% "http4s-blaze-client" % http4sVersion,
      "org.http4s"    %% "http4s-blaze-server" % http4sVersion,
      "org.http4s"    %% "http4s-circe"        % http4sVersion,
      "org.typelevel" %% "log4cats-slf4j"      % log4catsVersion
    )
  )
  .jsSettings(
    libraryDependencies ++= Seq(
      ("org.scala-js" %%% "scalajs-dom"                 % scalaJsDomVersion).cross(CrossVersion.for3Use2_13),
      ("org.scala-js" %%% "scala-js-macrotask-executor" % scalaJsMacroTaskExecutor).cross(CrossVersion.for3Use2_13)
    )
  )

lazy val coreJvm = core.jvm.settings(mimaSettings)
lazy val coreJs = core.js.disablePlugins(MimaPlugin)

lazy val examples = project
  .dependsOn(coreJvm)
  .disablePlugins(MimaPlugin)
  .settings(
    name := "canoe-examples",
    publish / skip := true,
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
  <<<<<<<.HEAD(crossScalaVersions) := Seq(scala2_12, scala2_13, scala3)
    =======
      crossScalaVersions := Seq(scala2_12, scala2_13)
    >>>>>>> master
)

lazy val crossDependencies =
  libraryDependencies ++= Seq(
    "co.fs2"        %%% "fs2-core"      % fs2Version,
    "org.typelevel" %%% "cats-core"     % catsCoreVersion,
    "org.typelevel" %%% "cats-effect"   % catsEffectVersion,
    "io.circe"      %%% "circe-core"    % circeVersion,
    "io.circe"      %%% "circe-generic" % circeVersion,
    "io.circe"      %%% "circe-parser"  % circeVersion,
    "org.typelevel" %%% "log4cats-core" % log4catsVersion
  ) ++ (CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, _)) =>
      Seq(
        compilerPlugin(("org.typelevel" % "kind-projector" % kindProjectorVersion).cross(CrossVersion.full)),
        compilerPlugin("org.augustjune" %% "context-applied" % contextAppliedVersion)
      )
    case _ => Seq.empty
  })

lazy val mimaSettings = Seq(
  mimaPreviousArtifacts := Set(organization.value %% name.value % "0.4.0")
)

lazy val compilerOptions =
  scalacOptions ++= Seq(
    "-deprecation", // Emit warning and location for usages of deprecated APIs.
    "-feature", // Emit warning and location for usages of features that should be imported explicitly.
    "-language:higherKinds", // Allow higher-kinded types
    "-language:postfixOps", // Allow higher-kinded types
    "-language:implicitConversions" // Allow definition of implicit functions called views
  ) ++ (if (scalaBinaryVersion.value.startsWith("2.12")) List("-Ypartial-unification", "-Xfatal-warnings")
        else if (scalaBinaryVersion.value.startsWith("3")) List("-Xmax-inlines", "128")
        else Nil)

lazy val tests = {
  val dependencies =
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest"                     % scalatestVersion,
      "org.typelevel" %% "cats-laws"                     % catsLawsVersion,
      "org.typelevel" %% "discipline-scalatest"          % disciplineVersion,
      "org.typelevel" %% "cats-effect-testing-scalatest" % "1.3.0"
    ).map(_ % Test)

  val frameworks =
    testFrameworks := Seq(TestFrameworks.ScalaTest)

  Seq(dependencies, frameworks)
}

<<<<<<< HEAD
val scala3 = "3.0.0"
val scala2_13 = "2.13.3"
val scala2_12 = "2.12.8"

val scala2Only = Seq(scala2_12, scala2_13)
val scala2And3 = scala2Only :+ scala3

val fs2Version = "3.0.6"
val catsCoreVersion = "2.6.1"
val catsEffectVersion = "3.2.9"
val catsLawsVersion = "2.6.1"
val circeVersion = "0.14.1"
val http4sVersion = "1.0.0-M23"
val log4catsVersion = "2.1.1"
val scalatestVersion = "3.2.10"
val disciplineVersion = "2.1.5"
val scalacheckShapelessVersion = "1.2.5"
val scalaJsDomVersion = "1.1.0"
val kindProjectorVersion = "0.13.0"
=======
ThisBuild / scalaVersion := scala2_13
ThisBuild / githubWorkflowTargetTags ++= Seq("v*")
ThisBuild / githubWorkflowPublishTargetBranches ++= Seq(RefPredicate.Equals(Ref.Branch("master")),
                                                        RefPredicate.StartsWith(Ref.Tag("v"))
)
ThisBuild / githubWorkflowPublish := Seq(WorkflowStep.Sbt(List("ci-release")))
ThisBuild / githubWorkflowEnv ++= List(
  "PGP_PASSPHRASE",
  "PGP_SECRET",
  "SONATYPE_PASSWORD",
  "SONATYPE_USERNAME"
).map(envKey => envKey -> s"$${{ secrets.$envKey }}").toMap

val scala2_13 = "2.13.8"
val scala2_12 = "2.12.15"

val fs2Version = "2.5.10"
val catsCoreVersion = "2.7.0"
val catsEffectVersion = "2.5.4"
val catsLawsVersion = "2.7.0"
val circeVersion = "0.14.1"
val http4sVersion = "0.21.33"
val log4catsVersion = "1.5.1"
val scalatestVersion = "3.2.11"
val disciplineVersion = "1.0.0-RC2"
val scalacheckShapelessVersion = "1.2.5"
val scalaJsDomVersion = "1.2.0"
val scalaJsMacroTaskExecutor = "1.0.0"
val kindProjectorVersion = "0.10.3"
>>>>>>> master
val contextAppliedVersion = "0.1.4"
