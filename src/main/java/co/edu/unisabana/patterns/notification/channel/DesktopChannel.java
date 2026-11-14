package co.edu.unisabana.patterns.notification.channel;

import co.edu.unisabana.patterns.notification.model.InteractionMode;
import co.edu.unisabana.patterns.notification.model.PresentationRequest;

import java.util.List;

public final class DesktopChannel extends PresentationChannel {

    public static final String NAME = "desktop";
    public static final String FORMAT_TOAST = "toast";
    public static final String FORMAT_CENTER = "notification-center";
    public static final String FORMAT_DIALOG = "dialog";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    protected String formatFor(InteractionMode mode) {
        return switch (mode) {
            case INFORMATIVE -> FORMAT_TOAST;
            case PERSISTENT -> FORMAT_CENTER;
            case BLOCKING -> FORMAT_DIALOG;
        };
    }

    @Override
    protected String render(
            PresentationRequest request,
            String format,
            String body,
            List<String> actions
    ) {
        return """
                Centro de notificaciones de escritorio
                formato: %s
                prioridad: %s
                para: %s
                %s
                %s
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
