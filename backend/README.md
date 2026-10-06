# Spring Boot backend

From this directory, run `./mvnw spring-boot:run` on macOS/Linux or
`.\mvnw.cmd spring-boot:run` on Windows. Requires JDK 17+ and first-run internet.
UI: http://localhost:8080/login.html. Health: http://localhost:8080/api/v1/health.

- `web`: REST and servlet session boundary.
- `application`: workflows, domain records, validation and assistant orchestration.
- `dataaccess`: repository contracts and replaceable storage/integration adapters.
- `storage`: seeded in-memory records; no database driver or connection.
- `config`: dependency wiring and hashed demo credential initialization.

Constructor injection keeps services independent of the memory implementation.
Data-access interfaces are the replacement point for a future database.

Run `./mvnw test` for integration tests. They verify controller-to-storage paths,
authentication, expiry, duplicate accounts and alert inbox lifecycle.
Read `../docs/API.md` for the frontend/backend contract.
