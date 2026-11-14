package co.edu.unisabana.patterns.notification.channel;

import co.edu.unisabana.patterns.notification.model.InteractionMode;
import co.edu.unisabana.patterns.notification.model.PresentationRequest;

import java.util.List;

public final class WebChannel extends PresentationChannel {

    public static final String NAME = "web";
    public static final String FORMAT_TOAST = "toast";
    public static final String FORMAT_BANNER = "banner";
    public static final String FORMAT_MODAL = "modal";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    protected String formatFor(InteractionMode mode) {
        return switch (mode) {
            case INFORMATIVE -> FORMAT_TOAST;
            case PERSISTENT -> FORMAT_BANNER;
            case BLOCKING -> FORMAT_MODAL;
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
                Canal web
                formato: %s
                clase: notification notification--%s
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
