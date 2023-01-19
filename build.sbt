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
      "org.scala-js"  %%% "scala-js-macrotask-executor" % scalaJsMacroTaskExecutor
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
    crossScalaVersions := Seq(scala2_12, scala2_13, scala3)
  )

lazy val projectSettings = Seq(
  organization := "org.augustjune",
  licenses ++= Seq(("MIT", url("http://opensource.org/licenses/MIT"))),
  homepage := Some(url("https://github.com/augustjune/canoe")),
  developers := List(
    Developer("augustjune", "Yura Slinkin", "jurij.jurich@gmail.com", url("https://github.com/augustjune"))
  ),
  scalaVersion := scala2_13,
  crossScalaVersions := Seq(scala2_12, scala2_13, scala3)
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
      "org.typelevel" %% "cats-effect-testing-scalatest" % "1.4.0"
    ).map(_ % Test)

  val frameworks =
    testFrameworks := Seq(TestFrameworks.ScalaTest)

  Seq(dependencies, frameworks)
}

ThisBuild / scalaVersion := scala3
ThisBuild / crossScalaVersions := Seq(scala2_13, scala3)
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

lazy val scala3 = "3.1.3"
lazy val scala2_13 = "2.13.8"
lazy val scala2_12 = "2.12.16"

lazy val scala2Only = Seq(scala2_12, scala2_13)
lazy val scala2And3 = scala2Only :+ scala3

lazy val fs2Version = "3.2.10"
lazy val catsCoreVersion = "2.7.0"
lazy val catsEffectVersion = "3.3.13"
lazy val catsLawsVersion = "2.8.0"
lazy val circeVersion = "0.14.1"
lazy val http4sVersion = "0.23.18"
lazy val log4catsVersion = "2.3.2"
lazy val scalatestVersion = "3.2.12"
lazy val disciplineVersion = "2.2.0"
lazy val scalacheckShapelessVersion = "1.2.5"
lazy val scalaJsDomVersion = "2.1.0"
lazy val scalaJsMacroTaskExecutor = "1.1.0"
lazy val kindProjectorVersion = "0.13.2"
lazy val contextAppliedVersion = "0.1.4"
