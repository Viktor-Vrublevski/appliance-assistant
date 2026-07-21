# Appliance Assistant

### Brief Description

Appliance Assistant is an intelligent home maintenance management system that simplifies appliance troubleshooting and
maintenance tracking. When household appliances malfunction or display cryptic error codes, finding the right solution
typically requires navigating lengthy PDF manuals or resorting to guesswork. This application eliminates that
frustration by combining AI-powered document retrieval with structured data management.

**Core Capabilities:**

- **Intelligent Troubleshooting**: Leverages Retrieval-Augmented Generation (RAG) to instantly locate relevant
  information from uploaded appliance manuals, warranty documents, and troubleshooting guides stored in a vector
  database.

- **Comprehensive Asset Tracking**: Maintains a SQL-based inventory of household appliances, including device names,
  purchase dates, maintenance schedules, and complete issue history.

Simply describe your appliance problem in natural language, and the system retrieves precise instructions from the
appropriate manual while maintaining an organized record of all maintenance activities.


## Local Run

Download Chroma DB from Docker Hub: [https://hub.docker.com/r/chroma/chroma](https://hub.docker.com/r/chromadb/chroma)
Run chroma db using Docker:
````bash
docker run -p 8000:8000 chromadb/chroma
````

### Set up PostgreSQL
Download PostgreSQL from Docker Hub: [https://hub.docker.com/_/postgres](https://hub.docker.com/_/postgres)
Run PostgreSQL using Docker:
````bash
docker run -p 5432:5432 -e POSTGRES_PASSWORD=postgres -d postgres
````

### Add API Anthropic Key

Set the ANTHROPIC_API_KEY environment variable before running the application.

Run application:
````bash
./mvnw spring-boot:run
````