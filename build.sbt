name := "scala-petclinic"

scalaVersion := "2.12.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"           % "10.0.5",
  "de.heikoseeberger" %% "akka-http-circe"     % "1.15.0",
  "io.circe"          %% "circe-generic"       % "0.7.0",
  "org.typelevel"     %% "cats"                % "0.9.0",
  "mysql"             % "mysql-connector-java" % "6.0.6" % "runtime"
)

javaOptions in run ++= Seq("-Djdbc.drivers=com.mysql.cj.jdbc.Driver")

fork in run := true
