package websocket.messages;

import chess.*;
import java.util.Objects;

public class LoadGameMessage extends ServerMessage {
    private final ChessGame game;

    public LoadGameMessage(ServerMessageType type, ChessGame game) {
        super(type); //calls the parent constructor
        this.game = game;
    }

    public ChessGame getChessGame() {
        return game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LoadGameMessage)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        LoadGameMessage that = (LoadGameMessage) o;
        return Objects.equals(game, that.game);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), game);
    }

    /*//not sure if I need a toString() method for this yet
    @Override
    public String toString() {
        return "ChessGameMessage{" +
               "serverMessageType=" + getServerMessageType() +
               ", chessGame=" + chessGame +
               '}';
    }
    */
}
