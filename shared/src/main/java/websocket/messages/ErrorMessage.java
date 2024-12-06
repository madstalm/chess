package websocket.messages;

import java.util.Objects;

public class ErrorMessage extends ServerMessage {
    private final String errorMessage;

    public ErrorMessage(ServerMessageType type, String errorMessage) {
        super(type); //calls the parent constructor
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
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
        return Objects.equals(errorMessage, that.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), errorMessage);
    }
}
