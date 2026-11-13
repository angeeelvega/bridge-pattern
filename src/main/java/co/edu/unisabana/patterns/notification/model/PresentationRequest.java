package co.edu.unisabana.patterns.notification.model;

import java.util.List;
import java.util.Objects;

/**
 * Contract that crosses the bridge. The abstraction describes what to show.
 * The channel decides how to show it, without knowing the concrete notification.
 */
public record PresentationRequest(
        String type,
        UrgencyLevel urgency,
        InteractionMode mode,
        String title,
        String body,
        String recipient,
        List<String> actions
) {

    public PresentationRequest {
        type = requireText(type, "type");
        if (urgency == null || mode == null) {
            throw new IllegalArgumentException("urgency and mode are required");
        }
        title = requireText(title, "title");
        body = requireText(body, "body");
        recipient = requireText(recipient, "recipient");
        if (actions == null || actions.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("actions are required");
        }
        actions = actions.stream()
                .map(action -> requireText(action, "action"))
                .toList();
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value.strip();
    }
}
