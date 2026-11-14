package co.edu.unisabana.patterns.notification;

import co.edu.unisabana.patterns.notification.abstraction.Notification;
import co.edu.unisabana.patterns.notification.channel.DesktopChannel;
import co.edu.unisabana.patterns.notification.channel.MobileChannel;
import co.edu.unisabana.patterns.notification.channel.PresentationChannel;
import co.edu.unisabana.patterns.notification.channel.WebChannel;
import co.edu.unisabana.patterns.notification.model.InteractionMode;
import co.edu.unisabana.patterns.notification.model.NotificationContent;
import co.edu.unisabana.patterns.notification.model.Presentation;
import co.edu.unisabana.patterns.notification.model.PresentationRequest;
import co.edu.unisabana.patterns.notification.model.UrgencyLevel;
import co.edu.unisabana.patterns.notification.type.AlertNotification;
import co.edu.unisabana.patterns.notification.type.ConfirmationNotification;
import co.edu.unisabana.patterns.notification.type.MessageNotification;
import co.edu.unisabana.patterns.notification.type.WarningNotification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationBridgeTest {

    private static final String MESSAGE = "The class moved to another room.";

    @ParameterizedTest
    @MethodSource("combinations")
    void eachTypeRendersOnEachChannelWithoutAClassPerCombination(Notification notification, String expectedChannel) {
        Presentation presentation = notification.display();

        assertEquals(expectedChannel, presentation.channel());
        assertFalse(presentation.format().isBlank());
        assertFalse(presentation.visibleBody().isBlank());
        assertTrue(presentation.rendered().contains(presentation.format()));
    }

    @Test
    void aMessageKeepsItsMeaningOnAnyChannel() {
        Presentation presentation = new MessageNotification(content(), new WebChannel()).display();

        assertEquals(UrgencyLevel.LOW, presentation.urgency());
        assertEquals(InteractionMode.INFORMATIVE, presentation.mode());
        assertEquals(WebChannel.FORMAT_TOAST, presentation.format());
        assertEquals(MESSAGE, presentation.visibleBody());
        assertEquals(List.of(), presentation.actions());
    }

    @Test
    void anAlertStaysVisibleAndAWarningAsksForAcknowledgement() {
        Presentation alert = new AlertNotification(content(), new DesktopChannel()).display();
        Presentation warning = new WarningNotification(content(), new DesktopChannel()).display();

        assertEquals(UrgencyLevel.HIGH, alert.urgency());
        assertEquals(InteractionMode.PERSISTENT, alert.mode());
        assertEquals(DesktopChannel.FORMAT_CENTER, alert.format());
        assertTrue(alert.visibleBody().endsWith("Esta alerta sigue visible hasta que la atiendas."));

        assertEquals(UrgencyLevel.MEDIUM, warning.urgency());
        assertEquals(List.of("Entendido"), warning.actions());
        assertTrue(warning.visibleBody().contains("Revisa las consecuencias"));
    }

    @Test
    void aConfirmationBlocksWithTheSameActionsOnEveryPlatform() {
        NotificationContent content = content();
        Presentation desktop = new ConfirmationNotification(content, new DesktopChannel()).display();
        Presentation mobile = new ConfirmationNotification(content, new MobileChannel()).display();
        Presentation web = new ConfirmationNotification(content, new WebChannel()).display();

        assertEquals(List.of("Aceptar", "Cancelar"), desktop.actions());
        assertEquals(desktop.actions(), mobile.actions());
        assertEquals(desktop.actions(), web.actions());
        assertEquals(InteractionMode.BLOCKING, desktop.mode());
        assertEquals(DesktopChannel.FORMAT_DIALOG, desktop.format());
        assertEquals(MobileChannel.FORMAT_ACTION_SHEET, mobile.format());
        assertEquals(WebChannel.FORMAT_MODAL, web.format());
    }

    @Test
    void changingTheChannelAtRuntimeDoesNotChangeTheContent() {
        NotificationContent content = content();
        Notification alert = new AlertNotification(content, new WebChannel());

        Presentation onWeb = alert.display();
        alert.changeChannel(new MobileChannel());
        Presentation onMobile = alert.display();

        assertEquals(WebChannel.NAME, onWeb.channel());
        assertEquals(WebChannel.FORMAT_BANNER, onWeb.format());
        assertEquals(MobileChannel.NAME, onMobile.channel());
        assertEquals(MobileChannel.FORMAT_PINNED_PUSH, onMobile.format());
        assertEquals(onWeb.urgency(), onMobile.urgency());
        assertEquals(onWeb.mode(), onMobile.mode());
        assertEquals(content, alert.content());
    }

    @Test
    void mobileTruncatesTheBodyAndDesktopKeepsIt() {
        String longMessage = "a".repeat(120);
        NotificationContent content = new NotificationContent("Notice", longMessage, "ana");
        String alertBody = longMessage + " Esta alerta sigue visible hasta que la atiendas.";

        Presentation desktop = new AlertNotification(content, new DesktopChannel()).display();
        Presentation mobile = new AlertNotification(content, new MobileChannel()).display();

        assertEquals(alertBody, desktop.visibleBody());
        assertEquals(MobileChannel.BODY_LIMIT, mobile.visibleBody().length());
        assertTrue(mobile.visibleBody().endsWith("…"));
    }

    @Test
    void oneChannelIsSharedByDifferentTypes() {
        PresentationChannel web = new WebChannel();
        Notification message = new MessageNotification(content(), web);
        Notification confirmation = new ConfirmationNotification(content(), web);

        assertEquals(WebChannel.FORMAT_TOAST, message.display().format());
        assertEquals(WebChannel.FORMAT_MODAL, confirmation.display().format());
        assertEquals(web, message.channel());
        assertEquals(web, confirmation.channel());
    }

    @Test
    void aNewTypeReusesTheExistingChannels() {
        Notification reminder = new ReminderNotification(content(), new MobileChannel());
        Presentation presentation = reminder.display();

        assertEquals("reminder", reminder.type());
        assertEquals(MobileChannel.FORMAT_PUSH, presentation.format());
        assertEquals(List.of("Snooze"), presentation.actions());
    }

    @Test
    void rejectsANullChannelOrContentAndBlankText() {
        NotificationContent content = content();
        WebChannel web = new WebChannel();

        assertThrows(NullPointerException.class, () -> new MessageNotification(content, null));
        assertThrows(NullPointerException.class, () -> new MessageNotification(null, web));
        assertThrows(NullPointerException.class, () -> new MessageNotification(content, web).changeChannel(null));
        assertThrows(IllegalArgumentException.class, () -> new NotificationContent("  ", MESSAGE, "ana"));
        assertThrows(IllegalArgumentException.class, () -> new NotificationContent("Notice", null, "ana"));
    }

    @Test
    void contentStripsWhitespaceAndTheRequestDoesNotExposeTheOriginalList() {
        NotificationContent content = new NotificationContent("  Hello  ", "  world ", " ana ");
        List<String> actions = new ArrayList<>();
        actions.add("  Accept  ");
        PresentationRequest request = new PresentationRequest(
                "confirmation",
                UrgencyLevel.MEDIUM,
                InteractionMode.BLOCKING,
                content.title(),
                content.message(),
                content.recipient(),
                actions
        );
        actions.add("Cancel");

        assertEquals("Hello", content.title());
        assertEquals("world", content.message());
        assertEquals("ana", content.recipient());
        assertEquals(List.of("Accept"), request.actions());
        assertThrows(UnsupportedOperationException.class, () -> request.actions().add("Other"));
    }

    private static Stream<Arguments> combinations() {
        NotificationContent content = new NotificationContent("Notice", MESSAGE, "ana");
        PresentationChannel desktop = new DesktopChannel();
        PresentationChannel mobile = new MobileChannel();
        PresentationChannel web = new WebChannel();
        List<PresentationChannel> channels = List.of(desktop, mobile, web);

        return channels.stream().flatMap(channel -> Stream.of(
                Arguments.of(new MessageNotification(content, channel), channel.name()),
                Arguments.of(new AlertNotification(content, channel), channel.name()),
                Arguments.of(new WarningNotification(content, channel), channel.name()),
                Arguments.of(new ConfirmationNotification(content, channel), channel.name())
        ));
    }

    private static NotificationContent content() {
        return new NotificationContent("Notice", MESSAGE, "ana");
    }

    private static final class ReminderNotification extends Notification {

        private ReminderNotification(NotificationContent content, PresentationChannel channel) {
            super(content, channel);
        }

        @Override
        public String type() {
            return "reminder";
        }

        @Override
        protected PresentationRequest prepare(NotificationContent content) {
            return buildRequest(
                    content,
                    UrgencyLevel.LOW,
                    InteractionMode.INFORMATIVE,
                    content.message(),
                    List.of("Snooze")
            );
        }
    }
}
