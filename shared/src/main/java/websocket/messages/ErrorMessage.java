package websocket.messages;

import java.util.Objects;

public class ErrorMessage extends ServerMessage {
    private final String message;

    public ErrorMessage(ServerMessageType type, String message) {
        super(type); //calls the parent constructor
        this.message = message;
    }

    public String getErrorMessage() {
        return message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ErrorMessage)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        ErrorMessage that = (ErrorMessage) o;
        return Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), message);
    }
}
