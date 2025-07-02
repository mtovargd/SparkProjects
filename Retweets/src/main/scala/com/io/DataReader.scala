package com.io

import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 * Handles reading the input data for retweets data analysis application.
 */
object DataReader {

  // Schema for message_dir, that contains each message info
  private val messageInfo = StructType(Array(
    StructField("MESSAGE_ID", LongType, nullable = false),
    StructField("MESSAGE", StringType, nullable = false)
  ))

  // Schema for message, contains original posted tweet info
  private val tweetInfo = StructType(Array(
    StructField("USER_ID", LongType, nullable = false),
    StructField("MESSAGE_ID", LongType, nullable = false)
  ))

  // Schema for retweet, contains retweets info (original poster, message posted, retweeter)
  private val retweetInfo = StructType(Array(
    StructField("USER_ID", LongType, nullable = false),
    StructField("SUBSCRIBER_ID", LongType, nullable = false),
    StructField("MESSAGE_ID", LongType, nullable = false)
  ))

  // Schema for each user data
  private val userInfo = StructType(Array(
    StructField("USER_ID", LongType, nullable = false),
    StructField("FIRST_NAME", StringType, nullable = false),
    StructField("LAST_NAME", StringType, nullable = false)
  ))

  /**
   * Reads the amounts CSV file from the given path.
   *
   * @param spark The SparkSession.
   * @param path  The path to the AVRO files.
   * @return A DataFrame with the specified data.
   */
  def readData(spark: SparkSession, path: String, fileToRead: String, format: String): DataFrame = {
    spark.read
      .format(if (format.equals("avro")) "avro" else "csv")
      .option("header", "true")
      .schema(fileToRead match {
        case "message_dir" => messageInfo
        case "message" => tweetInfo
        case "retweet" => retweetInfo
        case "user_dir" => userInfo
      })
      .load(path)
  }

}