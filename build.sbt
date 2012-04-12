resolvers += "yunrang-repo" at "http://dev.yunrang.com/nexus/content/groups/public2"

publishTo <<= (version) { version: String =>
  val nexus = "http://dev.yunrang.com/nexus/content/repositories/"
  if (version.trim.endsWith("SNAPSHOT")) Some("snapshots" at nexus + "snapshots/") 
  else                                   Some("releases"  at nexus + "releases/")
}

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
