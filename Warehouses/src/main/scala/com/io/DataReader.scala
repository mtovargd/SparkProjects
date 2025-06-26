package com.io

import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, SparkSession}

/**
 * Handles reading of input data for the warehouse analysis application.
 */
object DataReader {

  // Schema for amounts data
  private val amountSchema = StructType(Array(
    StructField("positionId", LongType, nullable = false),
    StructField("amount", DoubleType, nullable = false),
    StructField("eventTime", LongType, nullable = false)
  ))

  // Schema for the positions data
  private val positionSchema = StructType(Array(
    StructField("positionId", LongType, nullable = false),
    StructField("warehouse",  StringType, nullable = false),
    StructField("product", StringType, nullable = false),
    StructField("eventTime", LongType, nullable = false)
  ))

  /**
   * Reads the amounts CSV file from the given path.
   *
   * @param spark The SparkSession.
   * @param path  The path to the amounts CSV file.
   * @return A DataFrame with the amounts data.
   */
  def readData(spark: SparkSession, path: String, fileToRead: String): DataFrame = {
    spark.read
      .format("csv")
      .option("header", "true")
      .schema(if(fileToRead.equals("amounts")) amountSchema else positionSchema)
      .load(path)
  }

}