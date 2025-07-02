package com.processing

import org.apache.spark.sql.{DataFrame, SparkSession}
import DataProcessing._
import com.io.{DataReader, DataWriter}
import org.apache.spark.sql.functions._

/**
 * Manage the full retweets analyzer pipeline.
 */
object DataManager {

  /**
   * Main logic of the application.
   *
   * @param spark The SparkSession.
   * @param messagesDataPath Path to the input message_dir CSV.
   * @param tweetsDataPath Path to the input message CSV.
   * @param retweetsDataPath Path for the input of retweets CSV.
   * @param usersDataPath Path for the input of user_dir CSV.
   * @param outputPath Path for the output of the analysis.
   */
  def run(spark: SparkSession, messagesDataPath: String, tweetsDataPath: String,
          retweetsDataPath: String, usersDataPath: String, outputPath: String,
          fileFormat: String): Unit = {

    // 1. Read Data and rename repeated column names
    val messageDirData = DataReader.readData(spark, messagesDataPath, "message_dir",fileFormat)
    val messageData = DataReader.readData(spark, tweetsDataPath, "message",fileFormat)
      .withColumnRenamed("USER_ID", "OG_POSTER_ID")
    val retweetData = DataReader.readData(spark, retweetsDataPath, "retweet",fileFormat)
    val userDirData = DataReader.readData(spark, usersDataPath, "user_dir",fileFormat)

    println("Messages shared:")
    messageDirData.show()
    println("Tweets Data:")
    messageData.show()
    println("Retweets Data:")
    retweetData.show()
    println("Users Data:")
    userDirData.show()

    /* Quick data analysis (first look at the source data):
    * - The output asks for a total count of retweets (ONLY THE FIRST TWO WAVES).
    * - A wave would be the connection between user's retweets and original tweet.
    * - This counting can be calculated just with the message and retweet sources.
    * */

    // 2. Process the data
    val retweetsCounter: DataFrame = calculateTopUsers(messageData, retweetData)

    // 3. Once calculated the number of retweets, complete the info by joining user info and message info
    val topUsers: DataFrame = retweetsCounter
      .join(userDirData.withColumnRenamed("USER_ID","DB_USER_ID"), // adding the name and last name
        retweetsCounter("USER_ID") === col("DB_USER_ID"))
      .join(messageDirData.withColumnRenamed("MESSAGE_ID", "OG_TWEET_ID"), // adding the text of the tweet
        col("MESSAGE_ID") === col("OG_TWEET_ID"))
      .select(col("USER_ID"), col("FIRST_NAME"), col("LAST_NAME"), col("MESSAGE_ID"), col("NUMBER_RETWEETS"))
      .orderBy(desc(("NUMBER_RETWEETS")))

    println("TOP Users by number of retweets at the first 2 waves:")
    topUsers.show()

    println("Exporting results...")

    // 4. Write Results
    DataWriter.writeCsv(topUsers, outputPath)

    println("Finished exporting to output path.")
  }
}