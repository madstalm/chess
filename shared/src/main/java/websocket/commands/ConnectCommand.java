package websocket.commands;
import chess.ChessGame;

public class ConnectCommand extends UserGameCommand {
    private final ChessGame.TeamColor team;

    public ConnectCommand(CommandType commandType, String authToken, Integer gameID, ChessGame.TeamColor team) {
        super(commandType, authToken, gameID); //calls the parent constructor
        this.team = team;
    }

    public ChessGame.TeamColor getTeamColor() {
        return team;
    }

    public String getTeamColorString() {
        return switch (team) {
            case ChessGame.TeamColor.WHITE -> "white";
            case ChessGame.TeamColor.BLACK -> "black";
        };
    }
}
