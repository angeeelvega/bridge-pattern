package co.edu.unisabana.patterns.notification.channel;

import co.edu.unisabana.patterns.notification.model.InteractionMode;
import co.edu.unisabana.patterns.notification.model.Presentation;
import co.edu.unisabana.patterns.notification.model.PresentationRequest;

import java.util.List;
import java.util.Objects;

/**
 * Bridge implementor. Each platform translates the same request into its own format
 * and does not depend on a concrete notification type.
 */
public abstract class PresentationChannel {

    public final Presentation present(PresentationRequest request) {
        Objects.requireNonNull(request, "presentation request is required");
        String format = formatFor(request.mode());
        String body = adaptBody(request.body());
        List<String> actions = request.actions();
        String rendered = render(request, format, body, actions);
        return new Presentation(
                name(),
                format,
                request.urgency(),
                request.mode(),
                request.title(),
                body,
                actions,
                rendered
        );
    }

    public abstract String name();

    protected abstract String formatFor(InteractionMode mode);

    protected String adaptBody(String body) {
        return body;
    }

    protected abstract String render(
            PresentationRequest request,
            String format,
            String body,
            List<String> actions
    );

    protected final String visibleActions(List<String> actions) {
        if (actions.isEmpty()) {
            return "(ninguna)";
        }
        return String.join(", ", actions);
    }
}
