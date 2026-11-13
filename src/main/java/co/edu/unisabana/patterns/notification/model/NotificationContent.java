package co.edu.unisabana.patterns.notification.model;

public record NotificationContent(String title, String message, String recipient) {

    public NotificationContent {
        title = requireText(title, "title");
        message = requireText(message, "message");
        recipient = requireText(recipient, "recipient");
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value.strip();
    }
}
