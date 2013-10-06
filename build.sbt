
// Keep in sync with the Maven pom.xml file!
// See http://www.scala-sbt.org/release/docs/Community/Using-Sonatype.html for how to publish to
// Sonatype, using sbt only.

name := "yaidom-xlink"

organization := "eu.cdevreeze.yaidom"

version := "0.7.0-SNAPSHOT"

scalaVersion := "2.10.0"

// crossScalaVersions := Seq("2.10.0")

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

libraryDependencies += "eu.cdevreeze.yaidom" %% "yaidom" % "0.7.0"

libraryDependencies += "junit" % "junit" % "4.10" % "test"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test"

publishMavenStyle := true

publishTo <<= version { (v: String) =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { repo => false }

pomExtra := {
  <url>https://github.com/dvreeze/yaidom-xlink</url>
  <licenses>
    <license>
      <name>Apache License, Version 2.0</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
      <distribution>repo</distribution>
      <comments>Yaidom-xlink is licensed under Apache License, Version 2.0</comments>
    </license>
  </licenses>
  <scm>
    <connection>scm:git:git@github.com:dvreeze/yaidom-xlink.git</connection>
    <url>https://github.com/dvreeze/yaidom-xlink.git</url>
    <developerConnection>scm:git:git@github.com:dvreeze/yaidom-xlink.git</developerConnection>
  </scm>
  <developers>
    <developer>
      <id>dvreeze</id>
      <name>Chris de Vreeze</name>
      <email>chris.de.vreeze@caiway.net</email>
    </developer>
  </developers>
}
