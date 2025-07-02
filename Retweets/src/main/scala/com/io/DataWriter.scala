package com.io

import org.apache.spark.sql.{DataFrame, SaveMode}

/**
 * Handles writing of output data for retweets data analyzer application.
 */
object DataWriter {

  /**
   * Writes a DataFrame to a CSV file.
   * The output will be coalesced into a single file and will overwrite any existing data.
   *
   * @param df   The DataFrame to write.
   * @param path The destination path for the CSV file.
   */
  def writeCsv(df: DataFrame, path: String): Unit = {
    df.coalesce(1) // turns all the partitions into one before write
      .write
      .mode(SaveMode.Overwrite)
      .option("header", "true")
      .csv(path)
  }
}