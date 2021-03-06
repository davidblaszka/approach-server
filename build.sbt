
name := "approach-server"

version := "1.0"

scalaVersion := "2.12.4"

lazy val versions = new {
  val finatra = "17.12.0"
  val logback = "1.0.13"
  val guice = "4.0"
  val mockito = "1.9.5"
  val scalatest = "2.2.3"
  val specs2 = "2.3.12"
  val swagger = "0.5.0"
  val slick = "3.2.1"
  val postgresql = "42.1.4"
}

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "Twitter Maven" at "https://maven.twttr.com",
  Resolver.url("bintray-sbt-plugins", url("https://dl.bintray.com/eed3si9n/sbt-plugins/"))(Resolver.ivyStylePatterns)
)

libraryDependencies ++= Seq(
  "com.twitter" %% "finatra-http" % versions.finatra,
  "com.twitter" %% "finatra-jackson" % versions.finatra,
  "com.twitter" %% "inject-server" % versions.finatra,
  "com.twitter" %% "inject-app" % versions.finatra,
  "com.twitter" %% "inject-modules" % versions.finatra,
  "ch.qos.logback" % "logback-classic" % versions.logback,
  "com.google.inject" % "guice" % "4.1.0",
  "org.postgresql" % "postgresql" % versions.postgresql,

  "com.typesafe.slick" %% "slick" % versions.slick,
  "com.typesafe.slick" %% "slick-hikaricp" % versions.slick,

  "com.h2database" % "h2" % "1.4.181",

  //  anton's tiny type stuff
  "io.paradoxical" %% "paradox-scala-global" % "1.1",

  "io.paradoxical" %%  "finatra-server" % "1.0.4"
)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
