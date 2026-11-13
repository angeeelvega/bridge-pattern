package co.edu.unisabana.patterns.notification.type;

import co.edu.unisabana.patterns.notification.abstraction.Notification;
import co.edu.unisabana.patterns.notification.channel.PresentationChannel;
import co.edu.unisabana.patterns.notification.model.InteractionMode;
import co.edu.unisabana.patterns.notification.model.NotificationContent;
import co.edu.unisabana.patterns.notification.model.PresentationRequest;
import co.edu.unisabana.patterns.notification.model.UrgencyLevel;

import java.util.List;

public final class MessageNotification extends Notification {

    public static final String TYPE = "message";

    public MessageNotification(NotificationContent content, PresentationChannel channel) {
        super(content, channel);
    }

    @Override
    public String type() {
        return TYPE;
    }

    @Override
    protected PresentationRequest prepare(NotificationContent content) {
        return buildRequest(
                content,
                UrgencyLevel.LOW,
                InteractionMode.INFORMATIVE,
                content.message(),
                List.of()
        );
    }
}
