package co.edu.unisabana.patterns.notification.model;

import java.util.List;
import java.util.Objects;

public record Presentation(
        String channel,
        String format,
        UrgencyLevel urgency,
        InteractionMode mode,
        String visibleTitle,
        String visibleBody,
        List<String> actions,
        String rendered
) {

    public Presentation {
        channel = requireText(channel, "channel");
        format = requireText(format, "format");
        Objects.requireNonNull(urgency, "urgency is required");
        Objects.requireNonNull(mode, "mode is required");
        visibleTitle = requireText(visibleTitle, "visible title");
        visibleBody = requireText(visibleBody, "visible body");
        if (actions == null) {
            throw new IllegalArgumentException("actions are required");
        }
        actions = List.copyOf(actions);
        rendered = requireText(rendered, "rendered output");
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value.strip();
    }
}
