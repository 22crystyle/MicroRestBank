# Database Migration Consistency Guidelines

This document outlines the guidelines for managing database migrations using Liquibase in the RestBank microservices project. Adhering to these guidelines ensures consistency, reliability, and maintainability of our database schemas across all services.

## 1. Changelog File Naming Convention

All Liquibase changelog files must follow a strict naming convention to ensure proper ordering and easy identification.

**Format:** `v<major>.<minor>/<sequential_number>-<description>.yaml`

*   `<major>.<minor>`: Represents the version of the schema change (e.g., `1.0`, `1.1`). This should align with significant feature releases or breaking changes.
*   `<sequential_number>`: A three-digit sequential number (e.g., `001`, `002`, `003`) unique within its `v<major>.<minor>` directory. This ensures the order of execution for changes within a specific version.
*   `<description>`: A concise, hyphen-separated description of the change (e.g., `create-users-table`, `add-index-to-email`).

**Examples:**
*   `v1.0/001-create-customers-table.yaml`
*   `v1.0/002-add-email-index.yaml`
*   `v1.1/001-add-new-feature-table.yaml`

## 2. Changelog Structure and Best Practices

All changelog files should adhere to the following structural and content best practices:

*   **Format:** Use YAML for all changelog files.
*   **`databaseChangeLog` Root:** Each file must start with the `databaseChangeLog` root element.
*   **`changeSet` Attributes:**
    *   `id`: Must be unique across all changelogs in the project. A good practice is to combine the date and a sequential number (e.g., `YYYYMMDD-NN`).
    *   `author`: Clearly identify the author of the changeSet.
*   **`preConditions`:** Always include `preConditions` to ensure idempotency and prevent errors when applying migrations multiple times or in different environments.
    *   For table creation, use `not: - tableExists: { tableName: your_table_name }`.
    *   For column creation, use `not: - columnExists: { tableName: your_table_name, columnNames: your_column_name }`.
*   **Rollback:** Provide a `rollback` section for each `changeSet` to define how to revert the changes. This is crucial for disaster recovery and development flexibility.
*   **Comments:** Use comments within the YAML file to explain complex logic or the reasoning behind specific changes.

**Example `changeSet` Structure:**

```yaml
databaseChangeLog:
  - changeSet:
      id: 20250930-01-create-example-table
      author: YourName
      preConditions:
        - onFail: HALT
        - dbms:
            type: postgresql
        - not:
            - tableExists:
                schemaName: your_schema_name
                tableName: example_table
      changes:
        - createTable:
            schemaName: your_schema_name
            tableName: example_table
            columns:
              - column:
                  name: id
                  type: UUID
                  constraints:
                    primaryKey: true
                    nullable: false
              - column:
                  name: name
                  type: VARCHAR(255)
                  constraints:
                    nullable: false
      rollback:
        - dropTable:
            schemaName: your_schema_name
            tableName: example_table
```

## 3. Liquibase Configuration in `application.yml`

Each service's `application.yml` should contain the following Liquibase configuration, adapted for its specific schema:

```yaml
spring:
  liquibase:
    liquibase-schema: your_schema_name # The schema where Liquibase metadata tables are stored
    default-schema: your_schema_name   # The default schema for your service's tables
    change-log: classpath:/db/migration/db.changelog-master.yaml # Path to the master changelog
    enabled: true # Enable Liquibase migrations
```

## 4. Master Changelog (`db.changelog-master.yaml`)

The `db.changelog-master.yaml` file in each service's `src/main/resources/db/migration` directory should only include other changelog files. It should not contain any direct `changeSet` entries.

**Example `db.changelog-master.yaml`:**

```yaml
databaseChangeLog:
  - include:
      file: classpath:db/migration/v1.0/001-create-customers-table.yaml
  - include:
      file: classpath:db/migration/v1.0/002-create-outbox-table.yaml
  - include:
      file: classpath:db/migration/v1.1/003-add-new-column.yaml
```

## 5. Cross-Service Database Changes

*   **Principle:** Microservices should own their data. Avoid direct database dependencies between services.
*   **Data Synchronization:** If one service needs data from another, it should consume events (e.g., via Kafka/Debezium) and maintain its own denormalized copy of that data.
*   **Schema Evolution:** If a schema change in Service A impacts Service B's denormalized data, Service B should have its own Liquibase migration to adapt its local schema and data.

By following these guidelines, we ensure a robust and consistent approach to database schema evolution across the RestBank microservices.
