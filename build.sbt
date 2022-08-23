val scala3Version = "3.1.2"

val http4sVersion = "1.0.0-M33"

lazy val root = project
  .in(file("."))
  .settings(
    name := "gym-progress-tracker",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "1.0.0-M6" % Test,
      // http4s
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      // fs2
      "co.fs2" %% "fs2-core" % "3.2.11",
      // doobie
      "org.tpolecat" %% "doobie-core" % "1.0.0-RC1",
      "org.tpolecat" %% "doobie-postgres" % "1.0.0-RC1",
      // slf4j logging
      "org.slf4j" % "slf4j-simple" % "2.0.0"
    )
  )
