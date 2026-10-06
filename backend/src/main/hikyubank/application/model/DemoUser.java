package hikyubank.application.model;

/** Immutable data exchanged through repository boundaries. */
public record DemoUser(
    String username,
    String name,
    String firstName,
    String lastName,
    String passwordHash,
    String pinHash
) {
}
