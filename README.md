# Spark Projects

This repository contains Apache Spark projects for learning and development.

## Environment Setup

- **Apache Spark**: 4.0.0
- **Scala**: 2.13.16 (bundled with Spark) / 3.7.0 (system)
- **Java**: OpenJDK 21.0.7
- **SBT**: 1.11.2
- **Build Tool**: SBT (Scala Build Tool)

## Projects

### 1. Warehouses Data Analysis
Located in the `Warehouses/` directory - A Scala/Spark project for warehouse data processing and analytics.

**Features:**
- Processes warehouse amounts and positions data from CSV files
- Calculates current amounts by warehouse and product
- Generates warehouse statistics (min, max, average amounts)
- Runs on Docker-based Spark cluster

**Tech Stack:**
- Apache Spark 4.0.0
- Scala 2.13.16
- Docker & Docker Compose
- SBT Assembly Plugin

### 2. Retweets Analysis
Located in the `retweets/` directory - A Scala/Spark project for analyzing Twitter retweet patterns and waves.

**Features:**
- Processes Twitter data including messages, retweets, and user information
- Supports both CSV and Avro file formats
- Classifies retweets according to the needed waves (by now just the first 2)
    * Wave 1: Direct retweet from a root tweet or post.
    * Wave 2: Retweet from a wave 1 retweet.
- Analyzes retweet waves and identifies top users by retweet count
- Calculates retweet patterns for the first two waves of viral content
- Runs on Docker-based Spark cluster with Avro support

**Tech Stack:**
- Apache Spark 4.0.0 with Avro support
- Scala 2.13.16
- Docker & Docker Compose
- SBT Assembly Plugin
- Apache Avro for data serialization

## Prerequisites

Before running any project, ensure you have:

- **Docker Desktop**: Required for running the Spark cluster
- **Java 11+**: For local development (Java 21+ recommended)
- **SBT 1.11.2+**: Scala Build Tool
- **Git**: For version control

## Retweets Project - Quick Start

### 1. Setup and Build

```bash
# Navigate to the Retweets project
cd retweets/

# Ensure Docker Desktop is running
open -a Docker

# Build the application JAR with assembly
sbt clean assembly
```

### 2. Start Spark Cluster with Avro Support

```bash
# Start the Docker-based Spark cluster
./start-spark-cluster.sh

# Verify cluster is running
docker ps
```

### 3. Run the Application

```bash
# Build and submit the job automatically
./build-and-run.sh

# OR submit manually:
docker exec retweets-spark-master-1 /opt/spark/bin/spark-submit \
--class com.Main \
--master spark://spark-master:7077 \
--packages org.apache.spark:spark-avro_2.13:4.0.0 \
/opt/spark-apps/Retweets-assembly-0.1.0-SNAPSHOT.jar \
message message_dir retweet user_dir avro
```
- "message message_dir retweet user_dir avro" are the 5 parameters required to succesfully run the application.
- Just pass the name of the 4 files and the format (csv or avro, all the files must be at the same format).
- The program automatically fills the path (data/input) and the extension (.csv/.avro).

### 4. Monitor Results

- **Spark Master UI**: http://localhost:8080
- **Application UI**: http://localhost:4040 (when job is running)
- **Output Files**: Check `data/output/` directory
- **Open UIs**: Run `./open-spark-UIs.sh`

### 5. Stop Cluster

```bash
# Stop the Spark cluster when done
./stop-spark-cluster.sh
```


## Retweets Project Structure

```
retweets/
├── build.sbt                    # SBT build configuration with Spark 4.0.0 and Avro dependencies
├── project/
│   ├── build.properties         # SBT version
│   └── plugins.sbt             # SBT plugins (assembly)
├── src/main/scala/com/
│   ├── Main.scala              # Application entry point with command-line argument parsing
│   ├── io/
│   │   ├── DataReader.scala    # Handles reading CSV and Avro files with schema definitions
│   │   └── DataWriter.scala    # Data writing utilities for CSV output
│   └── processing/
│       ├── DataManager.scala   # Main data processing orchestrator
│       └── DataProcessing.scala # Core retweet wave analysis logic
├── data/
│   ├── input/                  # Input files (MESSAGE.csv/.avro, MESSAGE_DIR.csv/.avro, 
│   │                           #              RETWEET.csv/.avro, USER_DIR.csv/.avro)
│   └── output/                 # Generated analysis results
├── docker-compose.yml          # Docker Spark 4.0.0 cluster configuration
├── docker/
│   ├── Dockerfile              # Spark 4.0.0 + Java 17 container definition
│   └── start-spark.sh          # Spark cluster startup script
├── spark-apps/                 # Directory for JAR files
├── build-and-run.sh            # Automated build and submission script
├── start-spark-cluster.sh      # Script to start cluster
├── stop-spark-cluster.sh       # Script to stop cluster
└── open-spark-UIs.sh          # Helper script to open web UIs
```

## Retweets Project Data Format

#### Input Files

**MESSAGE.csv / MESSAGE.avro:**
```
USER_ID,MESSAGE_ID
204,1000
267,1001
153,1002
...
```
- `USER_ID`: The original poster/tweeter ID
- `MESSAGE_ID`: Unique identifier for the original tweet/message

**MESSAGE_DIR.csv / MESSAGE_DIR.avro:**
```
MESSAGE_ID,TEXT
1000,Message 1000
1001,Message 1001
1002,Message 1002
...
```
- `MESSAGE_ID`: Links to MESSAGE table
- `TEXT`: Content of the tweet/message (Note: Contains "NULL" values in sample data)

**RETWEET.csv / RETWEET.avro:**
```
USER_ID,SUBSCRIBER_ID,MESSAGE_ID
204,164,1000
204,198,1000
204,249,1000
249,279,1000
...
```
- `USER_ID`: The user being retweeted (original poster or previous retweeter)
- `SUBSCRIBER_ID`: The user doing the retweet
- `MESSAGE_ID`: The message being retweeted

**USER_DIR.csv / USER_DIR.avro:**
```
USER_ID,FIRST_NAME,LAST_NAME
1,Name1,Surname1
2,Name2,Surname2
3,Name3,Surname3
...
```
- `USER_ID`: Unique user identifier
- `FIRST_NAME`: User's first name
- `LAST_NAME`: User's last name

#### Output Files

**Top Users by Retweets (CSV):**
```
USER_ID,FIRST_NAME,LAST_NAME,MESSAGE_ID,NUMBER_RETWEETS
120,Name120,Surname120,1035,46
232,Name232,Surname232,1049,43
282,Name282,Surname282,1026,36
...
```
- Analysis results showing users ranked by total retweet count in first two waves
- Includes user information and the specific message that received the most retweets

#### Wave Analysis Logic

- **Wave 1**: Direct retweets from original posters (USER_ID matches MESSAGE.USER_ID)
- **Wave 2**: Retweets of Wave 1 retweets (USER_ID matches Wave 1 SUBSCRIBER_ID)
- The application currently analyzes only the first two waves of viral content spread

## Warehouses Project - Quick Start

### 1. Setup and Build

```bash
# Navigate to the Warehouses project
cd Warehouses/

# Ensure Docker Desktop is running
# You can start it from Applications or run:
open -a Docker

# Build the JAR file
sbt clean package

# Copy JAR to spark-apps directory
cp target/scala-2.13/warehouses_2.13-0.1.0-SNAPSHOT.jar spark-apps/
```

### 2. Start Spark Cluster

```bash
# Start the Docker-based Spark cluster
./start-spark-cluster.sh

# Verify cluster is running
docker ps
```

### 3. Run the Application

```bash
# Submit the job to the Spark cluster
docker exec warehouses-spark-master-1 /opt/spark/bin/spark-submit \
--class com.Main \
--master spark://spark-master:7077 \
--deploy-mode client \
/opt/spark-apps/warehouses_2.13-0.1.0-SNAPSHOT.jar \
/opt/spark-data/input/amounts.csv \
/opt/spark-data/input/positions.csv \
/opt/spark-data/output/current-amounts \
/opt/spark-data/output/stats
```

### 4. Monitor and Access

- **Spark Master UI**: http://localhost:8080
- **Application UI**: http://localhost:4040 (when job is running)
- **Output Files**: Check `data/output/` directory

### 5. Stop Cluster

```bash
# Stop the Spark cluster when done
./stop-spark-cluster.sh
```

## Alternative: Local Development

For local development and testing:

```bash
# Navigate to project directory
cd Warehouses/

# Compile the project
sbt compile

# Run locally (will use local Spark session)
sbt "run data/input/amounts.csv data/input/positions.csv data/output/current-amounts data/output/stats"
```

## Warehouses Project Structure

```
Warehouses/
├── build.sbt                    # SBT build configuration with dependencies
├── project/
│   ├── build.properties         # SBT version
│   └── plugins.sbt             # SBT plugins (assembly)
├── src/main/scala/com/
│   ├── Main.scala              # Application entry point
│   ├── io/
│   │   ├── DataReader.scala    # CSV data reading utilities
│   │   └── DataWriter.scala    # Data writing utilities
│   └── processing/
│       ├── DataManager.scala   # Main data processing orchestrator
│       └── DataProcessing.scala # Core business logic
├── data/
│   ├── input/                  # Input CSV files (amounts.csv, positions.csv)
│   └── output/                 # Generated output files
├── docker-compose.yml          # Docker Spark cluster configuration
├── docker/                     # Docker build files
├── spark-apps/                 # Directory for JAR files
├── start-spark-cluster.sh      # Script to start cluster
├── stop-spark-cluster.sh       # Script to stop cluster
└── open-spark-UIs.sh          # Helper script to open web UIs
```

## Warehouses Project Data Format

#### Input Files

**amounts.csv:**
```
positionId,amount,timeAmountsUpdated
1,2.93,1673182128
1,40.12,1673186578
...
```

**positions.csv:**
```
positionId,warehouse,product,timePositionsUpdated
1,W-5,P-2,1673385417
2,W-10,P-15,1673226446
...
```

#### Output Files

- **current-amounts/**: Latest amount for each position
- **stats/**: Warehouse statistics (min, max, avg amounts by warehouse and product)

## Troubleshooting

### Common Issues

1. **Docker not running:**
   ```bash
   Error: Cannot connect to the Docker daemon
   ```
   **Solution:** Start Docker Desktop: `open -a Docker`

2. **Port conflicts:**
   ```bash
   Error: Port 8080 already in use
   ```
   **Solution:** Stop other services using ports 8080 or 4040

3. **Java version conflicts:**
   ```bash
   UnsupportedClassVersionError
   ```
   **Solution:** Use `sbt package` instead of `sbt assembly`, or ensure Java 11+ in Docker

4. **JAR not found:**
   ```bash
   File not found: /opt/spark-apps/warehouses_2.13-0.1.0-SNAPSHOT.jar
   ```
   **Solution:** Ensure JAR is copied to spark-apps directory after building

5. **Permission denied on shell scripts:**
   ```bash
   Permission denied: ./start-spark-cluster.sh
   ```
   **Solution:** Make scripts executable: `chmod +x *.sh`

### Retweets Project Specific Issues

6. **Avro data source not found:**
   ```bash
   Failed to find data source: avro. Avro is built-in but external data source module since Spark 2.4
   ```
   **Solution:** Add Avro package to spark-submit:
   ```bash
   --packages org.apache.spark:spark-avro_2.13:4.0.0
   ```

7. **Java version compatibility with Spark 4.0.0:**
   ```bash
   UnsupportedClassVersionError: class file version 61.0, this version only recognizes up to 55.0
   ```
   **Solution:** Spark 4.0.0 requires Java 17+. Update Dockerfile to use:
   ```dockerfile
   FROM openjdk:17-jdk-slim as builder
   ```

8. **Spark 4.0.0 binary not found:**
   ```bash
   ERROR 404: Not Found - spark-4.0.0-bin-hadoop3.3-scala2.13.tgz
   ```
   **Solution:** Use correct binary filename for Spark 4.0.0:
   ```bash
   spark-4.0.0-bin-hadoop3.tgz  # Correct
   # NOT: spark-4.0.0-bin-hadoop3.3-scala2.13.tgz
   ```

9. **Python package not available in container:**
   ```bash
   E: Unable to locate package python3-simpy
   ```
   **Solution:** Remove unnecessary Python packages from Dockerfile or use pip instead:
   ```dockerfile
   RUN apt-get install -y python3 python3-pip
   RUN pip3 install simpy  # If needed
   ```

10. **Spark master container exits immediately:**
    ```bash
    Usage: Master [options]
    ```
    **Solution:** Check start script parameters and ensure proper environment variables are set

11. **Harmless shutdown error:**
    ```bash
    java.nio.file.NoSuchFileException: hadoop-client-api-3.4.1.jar
    ```
    **Solution:** This is a harmless cleanup race condition. Application completed successfully - can be ignored.

12. **Workers can't connect to master:**
    ```bash
    Failed to connect to master spark-master:7077
    ```
    **Solution:** Ensure master container is running and accessible:
    ```bash
    docker ps  # Check if master is running
    docker compose logs spark-master  # Check master logs
    ```

### Debugging

- Check cluster status: `docker ps`
- View container logs: `docker logs warehouses-spark-master-1`
- Access container shell: `docker exec -it warehouses-spark-master-1 bash`
- Monitor Spark UI: http://localhost:8080

## Development Environment

This repository is configured for development with:
- IntelliJ IDEA (`.idea/` directory present)
- SBT build configuration
- Git version control

## Notes

- Each project may have its own specific requirements and documentation
- Spark applications can be submitted using `spark-submit` or run directly through SBT
- Java version management is handled through jEnv for consistent development environment