ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.16"

lazy val root = (project in file("."))
  .settings(
    name := "Warehouses",
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % "4.0.0",
      "org.apache.spark" %% "spark-sql" % "4.0.0",
      "org.apache.spark" %% "spark-mllib" % "4.0.0" % "provided",
      "org.apache.spark" %% "spark-streaming" % "4.0.0" % "provided"
    ),
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", "services", xs @ _*)         => MergeStrategy.filterDistinctLines
      case PathList("META-INF", "versions", xs @ _*)         => MergeStrategy.first
      case PathList("META-INF", xs @ _*)                     => MergeStrategy.discard
      case "module-info.class"                               => MergeStrategy.discard
      case "org/apache/commons/logging/Log.class"            => MergeStrategy.first
      case "org/apache/commons/logging/LogFactory.class"     => MergeStrategy.first
      case "org/apache/commons/logging/LogConfigurationException.class" => MergeStrategy.first
      case "org/apache/commons/logging/impl/NoOpLog.class"   => MergeStrategy.first
      case "org/apache/commons/logging/impl/SimpleLog.class" => MergeStrategy.first
      case "com/google/thirdparty/publicsuffix/PublicSuffixPatterns.class" => MergeStrategy.first
      case "com/google/thirdparty/publicsuffix/PublicSuffixType.class" => MergeStrategy.first
      case "com/google/thirdparty/publicsuffix/TrieParser.class" => MergeStrategy.first
      case x => MergeStrategy.first
    }
  )
