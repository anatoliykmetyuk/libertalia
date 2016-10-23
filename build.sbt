val ScalaVer = "2.11.8"
val Scalarelational = "1.3.4"

lazy val commonSettings = Seq(
  name    := "Libertalia"
, version := "0.0.1"
, scalaVersion := "2.11.8"
, libraryDependencies ++= Seq(
    "org.typelevel"  %% "cats"      % "0.7.2"
  , "com.chuusai"    %% "shapeless" % "2.3.2"

  , "org.scalarelational" %% "scalarelational-core"       % Scalarelational
  , "org.scalarelational" %% "scalarelational-mapper"     % Scalarelational
  , "org.scalarelational" %% "scalarelational-mariadb"    % Scalarelational
  , "org.scalarelational" %% "scalarelational-versioning" % Scalarelational

  , "commons-io" % "commons-io" % "2.5"
  , "org.scala-lang" % "jline" % "2.11.0-M3"

  , "org.scalatest"  %% "scalatest" % "3.0.0"  % "test"
  )
, scalacOptions ++= Seq(
      "-deprecation",
      "-encoding", "UTF-8",
      "-feature",
      "-language:existentials",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-language:experimental.macros",
      "-unchecked",
      // "-Xfatal-warnings",
      "-Xlint",
      "-Yinline-warnings",
      "-Ywarn-dead-code",
      "-Xfuture")

// Kind projector
, resolvers += Resolver.sonatypeRepo("releases")
, addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.2")

// Simulacrum
, addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
, libraryDependencies += "com.github.mpilquist" %% "simulacrum" % "0.10.0"
)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(
    initialCommands := "import libertalia._"
  // , fork in run := true
  )
