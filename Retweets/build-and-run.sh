#!/bin/sh

echo "Building the application..."
sbt clean assembly

echo "Copying JAR to spark-apps directory..."
mkdir -p ./spark-apps
cp target/scala-2.13/Retweets-assembly-0.1.0-SNAPSHOT.jar ./spark-apps/

echo "Submitting job to Spark cluster..."
docker exec retweets-spark-master-1 /opt/spark/bin/spark-submit \
  --class com.Main \
  --master spark://spark-master:7077 \
  --packages org.apache.spark:spark-avro_2.13:4.0.0 \
  /opt/spark-apps/Retweets-assembly-0.1.0-SNAPSHOT.jar \
  message message_dir retweet user_dir avro

echo "Job submitted!"
