import sbt._

object Deps {
  lazy val http4sVersion             = "1.0.0-M38"
  lazy val log4catsVersion           = "2.4.0"
  lazy val scalatestVersion          = "3.2.17"
  lazy val circeVersion              = "0.14.6"
  lazy val circeGenericExtrasVersion = "0.14.3"
  lazy val circeYamlVersion          = "0.14.2"
  lazy val fs2Version                = "3.2.2"
  lazy val pulsarVersion             = "3.1.0"
  lazy val luceneVersion             = "9.8.0"
  lazy val awsVersion                = "2.20.162"
  lazy val prometheusVersion         = "0.16.0"

  val httpsDeps = Seq(
    "org.http4s" %% "http4s-dsl"          % http4sVersion,
    "org.http4s" %% "http4s-blaze-server" % http4sVersion,
    "org.http4s" %% "http4s-blaze-client" % http4sVersion,
    "org.http4s" %% "http4s-circe"        % http4sVersion
  )
}
