package com

import com.processing.DataManager

import org.apache.log4j.{Logger, Level}
import org.apache.spark.sql.SparkSession

/**
 * Main function for the retweet data analyzer
 *
 * 5 command-line arguments expected:
 1. Path to the input message.avro file.
 2. Path to the input message_dir.avro file.
 3. Path to the input retweet.avro file.
 4. Path to the input user_dir.avro file.
 5. Path to the output directory.
 6. Type of file (avro or csv).
 */
object Main {
  // Set up logging
  Logger.getLogger("org").setLevel(Level.ERROR)
  Logger.getLogger("akka").setLevel(Level.ERROR)

  def main(args: Array[String]): Unit = {
    if (args.length != 5) {
      println("Usage: Retweets <message_data_filename> <message_dir_data_filename> " +
        "<retweet_data_filename> <user_dir_data_filename> <file_format>")
      System.exit(1)
    }

    val messageInput = "data/input/" + args(0) + "." + args(4)
    val messageDirInput = "data/input/" + args(1) + "." + args(4)
    val retweetInput = "data/input/" + args(2) + "." + args(4)
    val userDirInput = "data/input/" + args(3) + "." + args(4)
    val output = "data/output"
    val fileFormat = args(4)

    // Set up Spark Session
    val spark = SparkSession.builder
      .appName("RetweetsByWavesAnalysis")
      .master(sys.env.getOrElse("SPARK_MASTER_URL", "local[*]"))
      .getOrCreate()

    try {
      // Run the main job logic
      DataManager.run(
        spark,
        messageDirInput,
        messageInput,
        retweetInput,
        userDirInput,
        output,
        fileFormat
      )
    } catch {
      case e: Exception =>
        println(s"Error occurred during the Spark job: ${e.getMessage}")
        e.printStackTrace()
        System.exit(1)
    } finally {
      // Stop the Spark session
      spark.stop()
    }
  }
}