package co.edu.unisabana.patterns.notification.abstraction;

import co.edu.unisabana.patterns.notification.channel.PresentationChannel;
import co.edu.unisabana.patterns.notification.model.InteractionMode;
import co.edu.unisabana.patterns.notification.model.NotificationContent;
import co.edu.unisabana.patterns.notification.model.Presentation;
import co.edu.unisabana.patterns.notification.model.PresentationRequest;
import co.edu.unisabana.patterns.notification.model.UrgencyLevel;

import java.util.List;
import java.util.Objects;

/**
 * Bridge abstraction. The notification type owns the meaning.
 * The channel, injected and replaceable, owns the presentation.
 */
public abstract class Notification {

    private final NotificationContent content;
    private PresentationChannel channel;

    protected Notification(NotificationContent content, PresentationChannel channel) {
        this.content = Objects.requireNonNull(content, "content is required");
        this.channel = Objects.requireNonNull(channel, "presentation channel is required");
    }

    public final Presentation display() {
        return channel.present(prepare(content));
    }

    public final void changeChannel(PresentationChannel channel) {
        this.channel = Objects.requireNonNull(channel, "presentation channel is required");
    }

    public final PresentationChannel channel() {
        return channel;
    }

    public final NotificationContent content() {
        return content;
    }

    public abstract String type();

    protected abstract PresentationRequest prepare(NotificationContent content);

    protected final PresentationRequest buildRequest(
            NotificationContent content,
            UrgencyLevel urgency,
            InteractionMode mode,
            String body,
            List<String> actions
    ) {
        return new PresentationRequest(
                type(),
                urgency,
                mode,
                content.title(),
                body,
                content.recipient(),
                actions
        );
    }
}
