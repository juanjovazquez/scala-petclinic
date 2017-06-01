name := "scala-petclinic"

scalaVersion := "2.12.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"           % "10.0.6",
  "com.typesafe.akka" %% "akka-http-testkit"   % "10.0.6" % Test,
  "de.heikoseeberger" %% "akka-http-circe"     % "1.15.0",
  "io.circe"          %% "circe-generic"       % "0.8.0",
  "org.typelevel"     %% "cats"                % "0.9.0",
  "org.scalatest"     %% "scalatest"           % "3.0.1" % Test,
  "mysql"             % "mysql-connector-java" % "6.0.6" % Runtime
)

javaOptions in run ++= Seq("-Djdbc.drivers=com.mysql.cj.jdbc.Driver")

scalacOptions ++= Seq(
  "-encoding",
  "UTF-8",
  "-feature",
  "-deprecation",
  "-unchecked",
  "-Xlint:-unused",
  "-Ywarn-unused",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Xfuture"
)

scalacOptions in (Compile, console) ~= (_.filterNot(_.contains("unused")))

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.3")

fork in run := true
