# QWEN.md - kuke-board-mine Project Context

## Project Overview

This is a large-scale traffic handling system called "kuke-board-mine", which is a multi-module Spring Boot application designed as a microservices architecture for a board/forums platform. The project handles articles, comments, likes, views, and hot articles as separate services to enable scalability and maintainability.

### Architecture
- **Multi-module Gradle project** with Spring Boot 3.3.2
- **Java 21** as the target and source compatibility
- **Microservices architecture** with separate services for different functionalities
- **MySQL** as the database for article and comment services
- **Snowflake module** for distributed ID generation

### Services Structure
The project is divided into multiple services:
1. **article** (port 9000) - Handles article creation, management
2. **comment** (port 9001) - Manages comments on articles
3. **like** (port 9002) - Handles like functionality
4. **view** (port 9003) - Tracks article views
5. **hot-article** (port 9004) - Manages trending/hot articles
6. **article-read** (port 9005) - Handles article reading functionality

### Common Components
- **snowflake** - A shared module for generating unique IDs in distributed systems

## Building and Running

### Prerequisites
- Java 21
- Gradle
- MySQL server running with databases: `article` and `comment`

### Build Commands
```bash
# Build the entire project
./gradlew build

# Build individual services
./gradlew :service:article:build
./gradlew :service:comment:build
./gradlew :service:like:build
./gradlew :service:view:build
./gradlew :service:hot-article:build
./gradlew :service:article-read:build

# Run individual services
./gradlew :service:article:run
./gradlew :service:comment:run
./gradlew :service:like:run
./gradlew :service:view:run
./gradlew :service:hot-article:run
./gradlew :service:article-read:run
```

### Database Setup
- Article service uses MySQL database named `article`
- Comment service uses MySQL database named `comment`
- Both require root user with password `root` (should be changed for production)

### Docker Deployment
The project includes a Dockerfile that:
1. Uses OpenJDK 17 as base image
2. Copies the article service JAR file
3. Exposes port 8080
4. Runs the application

## Development Conventions

### Code Style
- Uses Lombok for reducing boilerplate code
- Standard Spring Boot conventions
- JPA for database operations
- REST API design patterns

### Testing
- JUnit 5 for testing
- Spring Boot Test starter for integration tests
- Tests are located in the `src/test` directory of each service

### Configuration
- YAML configuration files for each service
- Each service runs on a different port to avoid conflicts
- Database configurations are specified in application.yml files

## Project Structure
```
kuke-board-mine/
├── build.gradle          # Main build configuration
├── settings.gradle       # Multi-module project settings
├── Dockerfile            # Docker configuration for article service
├── README.md
├── common/               # Shared modules
│   └── snowflake/        # Distributed ID generation
└── service/              # Individual microservices
    ├── article/          # Article management (port 9000)
    ├── comment/          # Comment management (port 9001)
    ├── like/             # Like functionality (port 9002)
    ├── view/             # View tracking (port 9003)
    ├── hot-article/      # Hot/trending articles (port 9004)
    └── article-read/     # Article reading (port 9005)
```

## Key Technologies
- Spring Boot 3.3.2
- Java 21
- Gradle
- MySQL
- JPA/Hibernate
- Lombok
- Docker