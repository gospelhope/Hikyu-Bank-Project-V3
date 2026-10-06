# Future external API adapters

Place backend REST provider adapters in this package when adding an external AI,
SMS, email or automation provider. Implement an application-layer port, such as
AssistantGateway, and select the adapter through Spring dependency injection.

The current RuleBasedAssistantGateway lives in the application layer because its
spending calculations are business logic. No external API adapter is active yet.
Repository interfaces and memory adapters are the current data-access implementation.
