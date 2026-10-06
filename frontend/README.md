# Presentation tier

The HTML/CSS/JavaScript UI is served by the Spring Boot backend.
Start from the project root with `./start.sh` or `start.cmd`, then open
http://localhost:8080/login.html. See the root README for complete instructions.

`js/api/` contains REST transport adapters. It does not validate credentials,
store financial records or enforce account eligibility. Those rules run in Java.
`js/views/` owns screen-specific rendering; `js/components/` owns reusable UI.
Browser sessionStorage stores only temporary sign-in navigation metadata and the
synthetic code delivery response. The actual authenticated session is server-owned.

Run `npm test` to verify the HTTP client contract. A standalone static server does
not supply the backend routes; do not use it as the complete application server.
