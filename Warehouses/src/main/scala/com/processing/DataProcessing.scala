package com.processing

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions._
import org.apache.spark.sql.expressions.Window

/**
 * Contains the core data transformation logic for warehouse data analysis.
 */
object DataProcessing {

  /**
   * Search for the most recent update recorded at each warehouse according to each product.
   */
  def getLastAmount(df: DataFrame): DataFrame = {

    val window = Window.partitionBy("positionId").orderBy(col("timeAmountsUpdated").desc)

    val processedAmountsData = df
      .withColumn("row_num", row_number().over(window))
      .filter(col("row_num") === 1)
      .drop("row_num")
      .withColumnRenamed("amount", "currentAmount")
    // Reorder and select just the required columns
    val reorderedCols = Seq("positionId", "warehouse", "product", "currentAmount")

    // Apply ordering and column selection
    processedAmountsData
      .select(reorderedCols.map(col): _*)
      .orderBy(col("positionId"))
  }

  /**
   * Calculate each warehouse's stats according to each product.
   */
  def getStats(df: DataFrame): DataFrame = {
    df.groupBy("warehouse", "product")
      .agg(
        max("amount").as("maximumAmount"),
        min("amount").as("minimumAmount"),
        round(avg("amount"), 2).as("averageAmount")
      )
  }
}