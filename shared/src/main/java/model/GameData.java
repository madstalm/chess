package model;

import com.google.gson.Gson;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, chess.ChessGame game) {

    public String toString() {
        return new Gson().toJson(this);
    }

    public GameData setWhitePlayer(String username) {
        return new GameData(this.gameID, username, this.blackUsername, this.gameName, this.game);
    }

    public GameData setBlackPlayer(String username) {
        return new GameData(this.gameID, this.whiteUsername, username, this.gameName, this.game);
    }

    public GameData setGameID(int id) {
        return new GameData(id, this.whiteUsername, this.blackUsername, this.gameName, this.game);
    }
}