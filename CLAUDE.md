# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Appliance Assistant is a home-maintenance assistant that answers appliance troubleshooting
questions using RAG over uploaded manuals, while tracking appliances and maintenance history
in a relational database. The intended flow is an agentic loop: retrieve relevant manual
passages from the vector store, generate step-by-step repair instructions, and automatically
log resolved issues back to SQL.

Status: early skeleton. Only the Spring Boot entry point (`ApplianceAssistantApplication`) and
a context-load test exist so far; the RAG, persistence, and web layers described below are
where new work is expected to go.

## Commands

Uses the Maven wrapper (`mvnw` / `mvnw.cmd`). On Windows PowerShell prefer `.\mvnw.cmd`.

- Build: `./mvnw clean package`
- Run the app: `./mvnw spring-boot:run` (serves on **port 9090**, see `application.properties`)
- Run all tests: `./mvnw test`
- Run a single test class: `./mvnw test -Dtest=ApplianceAssistantApplicationTests`
- Run a single test method: `./mvnw test -Dtest=ApplianceAssistantApplicationTests#contextLoads`

## Architecture

Spring Boot 3.5 (Java 21) servlet MVC application. Key technology choices, all driven by
`pom.xml`, that shape how features must be built:

- **Web / UI**: `spring-boot-starter-web` (servlet + Tomcat) with `spring-boot-starter-thymeleaf`
  for server-rendered views. This is deliberately a servlet app, not reactive — the Spring AI
  starters pull in WebFlux's `WebClient` transitively, so the servlet starter must stay present
  to keep the app from being auto-detected as reactive.
- **AI / RAG**: Spring AI (`spring-ai.version` = 1.1.8, managed via `spring-ai-bom`):
  - `spring-ai-starter-model-anthropic` — the chat model is **Anthropic Claude**. AI config
    (model, API key) goes in `application.properties` as `spring.ai.anthropic.*`.
  - `spring-ai-starter-vector-store-chroma` — vectors are stored in an external **Chroma**
    server (requires a running Chroma instance; connection configured via `spring.ai.vectorstore.chroma.*`).
  - `spring-ai-advisors-vector-store` — provides the RAG advisor that wires the vector store
    into the chat pipeline for retrieval-augmented answers.
- **Persistence**: `spring-boot-starter-data-jpa` over **PostgreSQL** (`postgresql` driver,
  runtime scope). Datasource + JPA settings belong in `application.properties`. The SQL side
  holds appliance inventory and maintenance/issue history — distinct from the vector store,
  which holds manual document embeddings.

The two data stores serve different roles and should not be conflated: **Chroma** = unstructured
manual content for retrieval; **PostgreSQL** = structured appliance/maintenance records.

## Package layout

All code lives under `epam.course` (`src/main/java/epam/course`). New components (controllers,
services, JPA entities/repositories, AI/RAG config) should be added as sub-packages there.