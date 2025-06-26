package com.processing

import org.apache.spark.sql.{DataFrame, SparkSession}
import DataProcessing._
import com.io.{DataReader, DataWriter}

/**
 * Manage the full warehouse data pipeline.
 */
object DataManager {

  /**
   * Main logic of the application.
   *
   * @param spark The SparkSession.
   * @param amountsDataPath Path to the input amounts CSV.
   * @param positionsDataPath Path to the input positions CSV.
   * @param outputAmountsPath Path for the output of current amounts.
   * @param outputWarehousesPath Path for the output of warehouse stats.
   */
  def run(spark: SparkSession, amountsDataPath: String, positionsDataPath: String,
          outputAmountsPath: String, outputWarehousesPath: String): Unit = {

    // 1. Read Data and rename repeated column names
    val amountsData = DataReader.readData(spark, amountsDataPath, "amounts")
      .withColumnRenamed("eventTime", "timeAmountsUpdated")

    val positionsData = DataReader.readData(spark, positionsDataPath, "positions")
      .withColumnRenamed("eventTime", "timePositionsUpdated")

    println("Amounts:")
    amountsData.show()
    println("Positions:")
    positionsData.show()

    // 2. Prepare and Join Data
    val amountsAtWarehouse = amountsData
      .join(positionsData, Seq("positionId"), "inner")

    // 3. Apply Transformations
    val currentAmounts: DataFrame = getLastAmount(amountsAtWarehouse)
    val warehousesStats: DataFrame = getStats(amountsAtWarehouse)

    println("Last amounts at warehouses:")
    currentAmounts.show()

    val sortedStats = warehousesStats.orderBy("warehouse", "product")
    println("Warehouses Stats")
    sortedStats.show()

    // 4. Write Results
    DataWriter.writeCsv(currentAmounts, outputAmountsPath)
    DataWriter.writeCsv(sortedStats, outputWarehousesPath)

    println("Finished exporting to output paths.")
  }
}