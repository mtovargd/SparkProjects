package com.processing

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._

/**
 * Contains the core data transformation logic for retweets data analysis.
 */
object DataProcessing {

  /**
   * Label each retweet according to the wave.
   * - Wave 1: Retweet directly from the original post.
   * - Wave 2: Retweet directly from a wave 1 post.
   */
  def calculateTopUsers(messageData: DataFrame, retweetData: DataFrame): DataFrame = {

    /*
    * * Proposed solution:
    * - 1. Do the count calculations by joining the message and retweet dataframes.
    * - 2. Add a new column to flag the wave of the post (only waves 1 and 2 needed).
    * - 3. Group and count the posts according to the original poster and the required waves.
    * - 4. Join the user data with the message data and the obtained count data.
    * */

    // ---- Wave 1: Direct retweets of original tweet ----
    val wave1 = retweetData
      .join(messageData, Seq("MESSAGE_ID"))
      .filter(col("USER_ID") === col("OG_POSTER_ID"))
      .select(col("USER_ID"), col("SUBSCRIBER_ID"), col("MESSAGE_ID"))
      .withColumn("WAVE", lit(1))

    println("Wave 1:")
    wave1.show()

    // ---- Wave 2: Retweets of Wave 1 retweeters ----
    val wave2 = retweetData
      .join(wave1.withColumnRenamed("SUBSCRIBER_ID", "WAVE1_USER_ID")
        .withColumnRenamed("MESSAGE_ID", "WAVE1_RT_ID").withColumnRenamed("USER_ID", "OG_POSTER_ID"),
        retweetData("USER_ID") === col("WAVE1_USER_ID") && retweetData("MESSAGE_ID") === col("WAVE1_RT_ID"))
      .select(col("OG_POSTER_ID").as(("USER_ID")), col("SUBSCRIBER_ID"), col("MESSAGE_ID"))
      .withColumn("WAVE", lit(2))

    println("Wave 2:")
    wave2.show()

   countRetweets(wave1.union(wave2))
  }

  private def countRetweets(calculatedWaves: DataFrame): DataFrame = {
    calculatedWaves
      .groupBy("USER_ID", "MESSAGE_ID")
      .count()
      .withColumnRenamed("count", "NUMBER_RETWEETS")
      .orderBy(desc("NUMBER_RETWEETS"))
  }

}