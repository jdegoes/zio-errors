val ZioVersion = "2.0.0-RC3"

ThisBuild / organization := "dev.zio"
ThisBuild / version      := "0.1.0"
ThisBuild / scalaVersion := "2.13.8"
ThisBuild / homepage     := Some(url("https://zio.github.io/zio-errors"))
ThisBuild / description  := "A workshop for error management with ZIO."
ThisBuild / licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))
ThisBuild / developers := List(
  Developer(
    "jdeoges",
    "John De Goes",
    "@jdegoes",
    url("https://github.com/jdegoes")
  )
)

addCommandAlias("fmt", "all root/scalafmtSbt root/scalafmtAll")
addCommandAlias("check", "all root/scalafmtSbtCheck root/scalafmtCheckAll")

scalacOptions in ThisBuild ++= Seq("-deprecation", "-feature")

lazy val root = project
  .in(file("."))
  .settings(
    publish / skip := true,
    console        := (workshop / Compile / console).value
  )
  .aggregate(workshop)

lazy val commonDeps = libraryDependencies ++= Seq(
  "dev.zio" %% "zio"          % ZioVersion,
  "dev.zio" %% "zio-test"     % ZioVersion % Test,
  "dev.zio" %% "zio-test-sbt" % ZioVersion % Test
)

lazy val workshop = (project in file("zio-errors-workshop"))
  .settings(
    name := "zio-errors-workshop",
    commonDeps,
    libraryDependencies ++= Seq(
      "dev.zio" %% "zio-test"     % ZioVersion % Test,
      "dev.zio" %% "zio-test-sbt" % ZioVersion % Test
    ),
    testFrameworks := Seq(new TestFramework("zio.test.sbt.ZTestFramework"))
  )
