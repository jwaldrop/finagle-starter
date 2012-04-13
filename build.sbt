resolvers += "twitter repo" at "http://maven.twttr.com"

seq(com.twitter.sbt.CompileThriftFinagle.newSettings: _*)

///////////////////////////////////////////////////////////////////////////////
libraryDependencies ++= Seq(
  "com.twitter" %% "finagle" % "3.0.0"
)

///////////////////////////////////////////////////////////////////////////////
name := "finagle-starter"

organization := "com.yunrang"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.9.1"

parallelExecution in Test := false

ivyLoggingLevel := UpdateLogging.Full
