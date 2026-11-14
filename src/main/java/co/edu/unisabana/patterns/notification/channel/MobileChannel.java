package co.edu.unisabana.patterns.notification.channel;

import co.edu.unisabana.patterns.notification.model.InteractionMode;
import co.edu.unisabana.patterns.notification.model.PresentationRequest;

import java.util.List;

public final class MobileChannel extends PresentationChannel {

    public static final String NAME = "mobile";
    public static final String FORMAT_PUSH = "push";
    public static final String FORMAT_PINNED_PUSH = "pinned-push";
    public static final String FORMAT_ACTION_SHEET = "action-sheet";
    public static final int BODY_LIMIT = 80;

    @Override
    public String name() {
        return NAME;
    }

    @Override
    protected String formatFor(InteractionMode mode) {
        return switch (mode) {
            case INFORMATIVE -> FORMAT_PUSH;
            case PERSISTENT -> FORMAT_PINNED_PUSH;
            case BLOCKING -> FORMAT_ACTION_SHEET;
        };
    }

    @Override
    protected String adaptBody(String body) {
        if (body.length() <= BODY_LIMIT) {
            return body;
        }
        return body.substring(0, BODY_LIMIT - 1) + "…";
    }

    @Override
    protected String render(
            PresentationRequest request,
            String format,
            String body,
            List<String> actions
    ) {
        return """
                Notificación móvil
                formato: %s
                insignia: %s
                para: %s
                %s — %s
                acciones: %s
                """.formatted(
                format,
                request.urgency().label(),
                request.recipient(),
                request.title(),
                body,
                visibleActions(actions)
        ).strip();
    }
}
