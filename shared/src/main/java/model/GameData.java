package model;

import com.google.gson.Gson;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, chess.ChessGame game) {

    public String toString() {
        return new Gson().toJson(this);
    }
}
