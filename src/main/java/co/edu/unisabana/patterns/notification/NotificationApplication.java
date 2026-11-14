package co.edu.unisabana.patterns.notification;

import co.edu.unisabana.patterns.notification.abstraction.Notification;
import co.edu.unisabana.patterns.notification.channel.DesktopChannel;
import co.edu.unisabana.patterns.notification.channel.MobileChannel;
import co.edu.unisabana.patterns.notification.channel.PresentationChannel;
import co.edu.unisabana.patterns.notification.channel.WebChannel;
import co.edu.unisabana.patterns.notification.model.NotificationContent;
import co.edu.unisabana.patterns.notification.model.Presentation;
import co.edu.unisabana.patterns.notification.type.AlertNotification;
import co.edu.unisabana.patterns.notification.type.ConfirmationNotification;
import co.edu.unisabana.patterns.notification.type.MessageNotification;
import co.edu.unisabana.patterns.notification.type.WarningNotification;

import java.util.List;

public final class NotificationApplication {

    private NotificationApplication() {
    }

    public static void main(String[] args) {
        NotificationContent content = new NotificationContent(
                "Mantenimiento programado",
                "El servicio académico no estará disponible esta noche, entre las 11 p. m. y la 1 a. m. Guarda tu trabajo antes.",
                "students@unisabana.edu.co"
        );

        System.out.println("=== La misma alerta cambia de canal en tiempo de ejecución ===");
        Notification alert = new AlertNotification(content, new DesktopChannel());
        print(alert.display());
        alert.changeChannel(new MobileChannel());
        print(alert.display());
        alert.changeChannel(new WebChannel());
        print(alert.display());

        System.out.println("=== Los cuatro tipos comparten un solo canal web ===");
        PresentationChannel web = new WebChannel();
        List<Notification> types = List.of(
                new MessageNotification(content, web),
                new AlertNotification(content, web),
                new WarningNotification(content, web),
                new ConfirmationNotification(content, web)
        );
        types.forEach(type -> print(type.display()));
    }

    private static void print(Presentation presentation) {
        System.out.println(presentation.rendered());
        System.out.println();
    }
}
