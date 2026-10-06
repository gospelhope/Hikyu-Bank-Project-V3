# Steps 5–6 — Evaluation and Reflection Draft

## Realistic use case

Chengyang is a student who wants to review monthly spending and keep track of
upcoming bills. He signs into the simulated bank, checks his checking and savings
balances, reviews credit card purchases, asks which spending category is largest,
and creates a low-balance alert. He tests that alert, opens the inbox and resolves
the reminder after reviewing it. No real bank account is connected.

## How the four-tier design supports the scenario

In Version 2, much of this workflow operated against browser-owned state. The new
UI sends requests to a backend that applies the same rules consistently across
screens and direct REST requests. Account availability and duplicate checking or
savings restrictions come from the same service and stored records. The assistant
reads server transactions instead of importing its own browser seed data. Alerts
and inbox messages remain separate domain objects, so updating a rule does not
rewrite a delivered test message. Server-owned sessions gate account access.

The repository contracts let the team later add durable persistence without
rebuilding the screen components. The gateway boundary similarly supports replacing
the rule-based assistant with an AI REST adapter. These improvements make changes
more localized, though the current memory implementation does not itself provide
production scalability.

## Limitations and foreseeable challenges

- No actual database is used; restarting the server loses new accounts and alerts.
- All sessions refer to one shared synthetic user; user-scoped data isolation is future work.
- Memory repositories and servlet sessions are local to one server instance.
- Login attempt limits are session-local and can be bypassed by creating a new session;
  production needs centralized throttling and broader authentication controls.
- OTP codes are intentionally returned for local demonstration. Real delivery must
  replace this response with an email/SMS provider and omit the code from responses.
- Alert dates and delivery preferences are saved, but no scheduler evaluates rules
  or sends external messages. Test generation is explicit.
- The assistant uses deterministic rules, not an external AI model or autonomous agent.
- Third-party integration adds credential handling, retries, idempotency, provider
  outages and observability responsibilities.

## Reflection draft — personalize before submission

Applying a four-tier architecture made the responsibilities of our project clearer.
Our frontend now focuses on the user experience, while the backend owns workflows
and authoritative validation. Repository interfaces separate those workflows from
storage, giving us a defined path to add a database and external integrations later.
This improves modularity and supports future scaling decisions, although our current
in-memory demo still has single-instance limits.

A challenge was identifying browser code that looked like an API adapter but also
performed validation or stored records. We moved those responsibilities into Java
services and repository adapters, while retaining form hints and display filtering
in the UI. Authentication required particular care because the two-page experience
needed to share workflow state without treating browser state as authorization.

AI assistance helped draft the module structure, REST contracts and integration
tests. It still required checking that controllers did not bypass services, that
server validation matched the UI, and that the system did not claim unimplemented
AI or automation capabilities. Figma supported the existing presentation design;
Bubble, Zapier and WordPress remain planned integration options. This draft should
be adjusted to reflect each team member's actual work and learning experience.
