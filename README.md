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

### 2. [Second Project]
Coming soon...

## Prerequisites

Before running any project, ensure you have:

- **Docker Desktop**: Required for running the Spark cluster
- **Java 11+**: For local development (Java 21+ recommended)
- **SBT 1.11.2+**: Scala Build Tool
- **Git**: For version control

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

## Project Structure

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

## Data Format

### Input Files

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

### Output Files

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

