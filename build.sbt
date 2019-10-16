
name := "yaidom-xlink"

organization := "eu.cdevreeze.yaidom"

version := "1.8.0-SNAPSHOT"

scalaVersion := "2.13.1"

crossScalaVersions := Seq("2.13.1", "2.12.10")

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-Xfatal-warnings", "-Xlint")

libraryDependencies += "eu.cdevreeze.yaidom" %% "yaidom" % "1.10.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "test"

mimaPreviousArtifacts := Set("eu.cdevreeze.yaidom" %%% "yaidom-xlink" % "1.7.0")

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

Test / publishArtifact := false

pomIncludeRepository := { _ => false }

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
