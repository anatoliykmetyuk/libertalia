val ScalaVer = "2.11.8"

lazy val commonSettings = Seq(
  name    := "Libertalia"
, version := "0.0.1"
, scalaVersion := "2.11.8"
, libraryDependencies ++= Seq(
    "org.typelevel"  %% "cats"      % "0.7.2"
  , "com.chuusai"    %% "shapeless" % "2.3.2"

  , "commons-io" % "commons-io" % "2.5"

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
      "-Xfatal-warnings",
      "-Xlint",
      "-Yinline-warnings",
      "-Ywarn-dead-code",
      "-Xfuture")

, resolvers += Resolver.sonatypeRepo("releases")
, addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.2")
)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(
    initialCommands := "import libertalia._"
  )
