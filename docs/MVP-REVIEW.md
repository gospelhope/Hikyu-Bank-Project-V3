# Step 1 — Version 2 MVP Review

The uploaded Version 2 used modular JavaScript filenames, but its responsibilities
were still coupled in the browser. This review is based on the supplied ZIP.

| Original location | Coupling or unclear boundary | Refactor |
| --- | --- | --- |
| `js/app.js` | Route orchestration, transaction filters and assistant rendering in one module | Route orchestration remains in `app.js`; screens move to `js/views/` |
| `js/api/auth-mock.js` | Browser compared passwords/PINs and owned session/challenge state | Java `AuthService` owns validation; HTTP session owns workflow state |
| `js/api/accounts.js` | API adapter also validated products, enforced duplicate rules and wrote localStorage | REST-only adapter; `AccountService` enforces rules through `AccountRepository` |
| `js/api/alerts.js` | Display labels, validation and persistence shared one module | UI hints in `components/alert-options.js`; Java validates and stores rules |
| `js/api/notifications.js` | UI events and notification CRUD both owned local records | UI event notifications remain; records and lifecycle move to Java service |
| `js/api/index.js` | API facade imported hardcoded transactions and calculated spending | Thin HTTP facade; `AssistantService` calls a replaceable gateway with repository data |
| `js/api/storage.js`, `mock.js` | Seed data and storage ran in the UI runtime | JSON seeds and MemoryDatabase move into the storage tier |

## Boundaries established

1. UI modules render returned data, collect form input and manage navigation.
2. Controllers translate REST requests; they do not access storage maps.
3. Application services enforce rules; they do not reference servlet APIs or
   MemoryDatabase.
4. Repository adapters communicate with storage and return immutable domain records.
5. MemoryDatabase stores records; it does not validate banking workflows.

Browser form constraints still improve usability. They are not authoritative;
server DTO constraints and services validate direct API requests as well.

## Remaining boundaries to improve later

The current store belongs to one public demo user and resets on restart.
A real multi-user design needs user-scoped queries, durable transactions and
unique account constraints. Local Java synchronization prevents duplicate inserts
within this instance; it is not a distributed locking strategy. Real OTP delivery,
external AI, background scheduling and persistent sessions are not implemented.
