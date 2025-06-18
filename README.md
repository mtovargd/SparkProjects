# Spark Projects

This repository contains Apache Spark projects for learning and development.

## Environment Setup

- **Apache Spark**: 4.0.0
- **Scala**: 2.13.16 (bundled with Spark) / 3.7.0 (system)
- **Java**: OpenJDK 21.0.7
- **SBT**: 1.11.2
- **Build Tool**: SBT (Scala Build Tool)

## Projects

### 1. Warehouses
Located in the `Warehouses/` directory - A Scala/Spark project for warehouse data processing.

### 2. [Second Project]
Coming soon...

## Getting Started

1. Ensure you have the required environment setup:
   - Java 21+ (managed via jEnv)
   - Apache Spark 4.0.0
   - SBT 1.11.2

2. Navigate to any project directory
3. Run `sbt compile` to compile the project
4. Run `sbt run` to execute the application

## Development Environment

This repository is configured for development with:
- IntelliJ IDEA (`.idea/` directory present)
- SBT build configuration
- Git version control

## Notes

- Each project may have its own specific requirements and documentation
- Spark applications can be submitted using `spark-submit` or run directly through SBT
- Java version management is handled through jEnv for consistent development environment

