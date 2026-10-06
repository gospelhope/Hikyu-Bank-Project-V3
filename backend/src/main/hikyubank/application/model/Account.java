package hikyubank.application.model;

/** Immutable data exchanged through repository boundaries. */
public record Account(
    String id,
    String type,
    String name,
    String suffix,
    java.math.BigDecimal balance,
    String status,
    java.math.BigDecimal limit,
    java.math.BigDecimal minimumDue,
    String dueDate
) {
}
