Nice.javaProject

name := "exporter"

description := "Bio4j Gexf/Graphml/GraphSON exporter"

organization := "bio4j"

bucketSuffix := "era7.com"

javaVersion := "1.8"

libraryDependencies ++= Seq(
  "com.thinkaurelius.titan" % "titan-core" % "0.5.0-M1",
  "com.tinkerpop.blueprints" % "blueprints-core" % "2.5.0",
  "com.tinkerpop.gremlin" % "gremlin-java" % "2.5.0",
  "com.tinkerpop.gremlin" % "gremlin-groovy" % "2.5.0",  
  "commons-cli" % "commons-cli" % "1.2",
  "junit" % "junit" % "4.11"
)
