package com

import com.processing.DataManager

import org.apache.log4j.{Logger, Level}
import org.apache.spark.sql.SparkSession

/**
 * Main function for the warehouses data analyzer
 *
 * 4 command-line arguments expected:
    1. Path to the input amounts.csv file.
    2. Path to the input positions.csv file.
    3. Path to the output directory for current amounts.
    4. Path to the output directory for warehouse stats.
 */
object Main {
  // Set up logging
  Logger.getLogger("org").setLevel(Level.ERROR)
  Logger.getLogger("akka").setLevel(Level.ERROR)

  def main(args: Array[String]): Unit = {
    if (args.length != 4) {
      println("Usage: SparkWarehouses <amounts_csv_path> <positions_csv_path> <output_current_amounts_path> <output_warehouse_stats_path>")
      System.exit(1)
    }

    val inputAmounts = args(0)
    val inputPosition = args(1)
    val outputAmounts = args(2)
    val outputWarehouses = args(3)

    /*val inputAmounts = "data/input/amounts.csv"
    val inputPosition = "data/input/positions.csv"
    val outputAmounts = "data/output/current-amounts"
    val outputWarehouses = "data/output/stats"*/


    // Set up Spark Session
    val spark = SparkSession.builder
      .appName("WarehousesDataAnalysis")
      .master(sys.env.getOrElse("SPARK_MASTER_URL", "local[*]"))
      .getOrCreate()

    try {
      // Run the main job logic
      DataManager.run(
        spark,
        inputAmounts,
        inputPosition,
        outputAmounts,
        outputWarehouses
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