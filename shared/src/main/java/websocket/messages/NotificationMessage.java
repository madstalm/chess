package websocket.messages;

import java.util.Objects;

public class NotificationMessage extends ServerMessage {
        private final String notification;

    public NotificationMessage(ServerMessageType type, String notification) {
        super(type); //calls the parent constructor
        this.notification = notification;
    }

    public String getNotification() {
        return notification;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof NotificationMessage)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        NotificationMessage that = (NotificationMessage) o;
        return Objects.equals(notification, that.notification);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), notification);
    }
}
