package hikyubank.application.model;

/** Immutable data exchanged through repository boundaries. */
public record Notification(
    String id,
    String alertId,
    String accountId,
    String title,
    String body,
    String createdAt,
    boolean read,
    boolean resolved,
    String source
) {

    public Notification withState(boolean nextRead, boolean nextResolved) {
        return new Notification(
            id, alertId, accountId, title, body, createdAt,
            nextRead, nextResolved, source
        );
    }
}
