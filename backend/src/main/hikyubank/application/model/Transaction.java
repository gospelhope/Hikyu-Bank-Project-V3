package hikyubank.application.model;

/** Immutable data exchanged through repository boundaries. */
public record Transaction(
    String id,
    String accountId,
    String name,
    String category,
    String date,
    java.math.BigDecimal amount,
    String icon
) {
}
