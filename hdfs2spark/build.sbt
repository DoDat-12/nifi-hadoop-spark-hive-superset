ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.12"

lazy val root = (project in file("."))
  .settings(
    name := "hdfs2spark",
    idePackagePrefix := Some("com.dodat.spark")
  )

val sparkVersion = "3.0.0"

libraryDependencies ++= Seq (
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-core" % sparkVersion
)