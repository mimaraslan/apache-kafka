import org.apache.logging.log4j.core.config.composite.MergeStrategy
import sun.security.tools.PathList

name := "HelloScala"

version := "0.1"

scalaVersion := "2.11.7"

dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-core" % "2.8.5"
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.8.5"
dependencyOverrides += "com.fasterxml.jackson.module" % "jackson-module-scala_2.11" % "2.8.5"


libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.18",
  "org.apache.spark" %% "spark-core" % "2.2.0",
  "org.apache.spark" %% "spark-sql" % "2.2.0",
  "org.apache.spark" %% "spark-streaming" % "2.2.0",
  "com.datastax.spark" %% "spark-cassandra-connector" % "2.3.2",
  "org.apache.spark" %% "spark-streaming-kafka-0-10" % "2.2.0",
  "org.apache.kafka" %% "kafka" % "0.11.0.0",
  "org.apache.kafka" % "kafka-streams" % "0.11.0.0",
  "org.apache.kafka" % "kafka-clients" % "0.11.0.0",
  "org.apache.kafka" % "connect-json" % "0.11.0.0"
)

