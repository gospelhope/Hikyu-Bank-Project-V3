# Hikyu Bank — Four-Tier Web MVP

A local personal finance demonstration using HTML, CSS, JavaScript and a Java
Spring Boot REST backend. All users, accounts, balances and transactions are
synthetic. No real bank connections or financial transactions are involved.

This version separates `frontend/` and `backend/`, moves workflow rules to Java,
and replaces browser financial storage with server-owned in-memory storage.
There is **no database connection**. Data resets when the Java process restarts.

## Local startup

### Requirements

- JDK 17 or later; check with `java -version`.
- Internet access on the first run to download Maven and Java dependencies.
- A modern web browser.
- Node.js is optional and used only for frontend tests.

Maven Wrapper is included. You do not need to install Maven separately.

### macOS or Linux

Extract the ZIP, open Terminal in the extracted project folder, then run:

```bash
cd Hikyu-Bank-Project
chmod +x start.sh backend/mvnw
./start.sh
```

If your terminal is already inside `Hikyu-Bank-Project`, omit the `cd` command.
Wait for the console to report that HikyuBankApplication has started.

Open **http://localhost:8080/login.html**.

Equivalent startup from the backend directory:

```bash
cd backend
./mvnw spring-boot:run
```

### Windows

In PowerShell or Command Prompt, from the extracted project folder:

```powershell
cd Hikyu-Bank-Project
.\start.cmd
```

Or start directly from the backend directory:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Open **http://localhost:8080/login.html** after startup completes.

> Run the Spring Boot server for this version. Opening HTML directly or using a
> standalone Python/Live Server static server will not provide `/api/v1` and can
> cause API 404 errors. Spring Boot serves the UI and REST API together.

### Demo sign-in

| Field | Value |
| --- | --- |
| Username | `chengyang.lee` |
| Password | `HikyuDemo2026!` |
| Email verification PIN | `2468` |

1. Sign in with the username and password.
2. Choose text message or email verification.
3. Select **Send verification code**.
4. Enter the six digits displayed in the simulated message box.
5. Email verification also requires the four-digit demo PIN.

Each code is generated on the backend, expires after five minutes and is consumed
on success. Requests can be resent after 30 seconds. No real text or email is sent.

The backend stores password/PIN hashes, and returns a session cookie rather than
an access token in browser storage. These public demo credentials are not suitable
for real banking. If `HIKYU_DEMO_PASSWORD` or `HIKYU_DEMO_PIN` is changed, type those
values manually; the UI autofill still contains the documented public defaults.

### Stop or reset

Press `Ctrl+C` in the server terminal. Restarting restores the initial seeded data.
Browser refresh preserves changes while the backend is running. All browsers using
the demo account share the same synthetic data in that Java process.

### Build and test

From `backend/`:

```bash
./mvnw test
./mvnw package
java -jar target/hikyu-bank-3.0.0.jar
```

Windows: use `.\mvnw.cmd` in place of `./mvnw`.

Frontend transport tests, from `frontend/`:

```bash
npm test
```

Rebuild/restart after editing frontend files: Maven copies `frontend/` into the
server resources. The packaged JAR includes the frontend assets.

If port 8080 is in use, stop the other server or use another port:

```bash
# macOS/Linux, from backend/
PORT=8081 ./mvnw spring-boot:run
```

```powershell
# Windows PowerShell, from backend/
$env:PORT = "8081"
.\mvnw.cmd spring-boot:run
```

Then open `http://localhost:8081/login.html`.

## Four tiers

| Tier | Code location | Responsibility |
| --- | --- | --- |
| Presentation / UI | `frontend/`; `backend/.../web/` HTTP boundary | Pages, accessible controls, rendering, HTTP request/response translation |
| Application logic | `backend/.../application/` | Authentication, account eligibility, alert validation and workflow orchestration |
| Data access | `backend/.../dataaccess/` | Repository contracts, memory adapters and future API adapters |
| Database / storage | `backend/.../storage/`; `resources/seed/` | Actual demo record storage in memory; future database replacement point |

**The fourth tier is an in-memory storage substitute, not an implemented SQL or
NoSQL database.** This follows the requested no-database scope. The architecture
reserves its responsibilities and interface boundaries for a later database.

## Project structure

```text
Hikyu-Bank-Project/
  frontend/
    index.html
    login.html
    verify.html
    assets/
    css/
    js/
      api/                    HTTP adapters only
      auth/                   Sign-in pages and navigation state
      components/             Reusable UI components
      views/                  Transaction and assistant screens
      app.js                  Dashboard route orchestration
      main.js                 Session-aware dashboard entry point
    tests/                    HTTP client tests
  backend/
    pom.xml
    mvnw
    mvnw.cmd
    .mvn/wrapper/
    src/main/hikyubank/
      web/                    Controllers, sessions and HTTP errors
      application/            Services, workflow state, DTOs and domain models
      dataaccess/             Repository interfaces and adapters
      storage/                MemoryDatabase
      config/                 Spring wiring and local demo configuration
    src/main/resources/
      application.yml
      seed/demo-data.json
    src/test/                 BankApiIntegrationTest.java and test resources
  docs/
    MVP-REVIEW.md
    ARCHITECTURE.md
    architecture.svg
    API.md
    EVALUATION-AND-REFLECTION.md
    FIGMA-INBOX.md
    VERIFICATION.md
  start.sh
  start.cmd
```

## Working features

- Two-page sign-in and verification, with server-side expiry and attempt limits.
- Account balances, debt details and account-filtered transactions.
- Server-owned checking/savings duplicate restrictions, also reflected in UI choices.
- Credit, loan and investment applications remain pending.
- Alert preferences, test notifications, unread/read/resolved states and deletion.
- Rule-based assistant insights calculated from the server's synthetic records.

Alert preferences are stored but are **not scheduled automatically**. Test buttons
explicitly create simulated inbox notifications. Delivery adapters for email, SMS,
calendar or Zapier remain future integration work.

## Architecture and coursework

- [Version 2 analysis](docs/MVP-REVIEW.md)
- [Architecture diagram and explanation](docs/ARCHITECTURE.md)
- [REST API contract](docs/API.md)
- [Use case, challenges and reflection draft](docs/EVALUATION-AND-REFLECTION.md)
- [Verification results](docs/VERIFICATION.md)

Figma supports UI prototyping. Bubble, Zapier, WordPress and external AI plugins
are planned integration options, not functioning integrations in this version.
No Figma design file was edited during this architecture refactor.

Spring Boot reference: https://docs.spring.io/spring-boot/3.5/system-requirements.html

## Short backend paths

See docs/SHORT-PATHS.md. Production Java sources use src/main/hikyubank/;
the integration test is directly inside src/test/. The root package is hikyubank.
Maven generates target/classes/hikyubank/. The included target/classes folder
is build output and can be regenerated; the local startup command is unchanged.
