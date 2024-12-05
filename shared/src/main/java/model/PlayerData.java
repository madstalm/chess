package model;

import com.google.gson.Gson;

import chess.ChessGame;

public record PlayerData(String username, Integer gameID, ChessGame.TeamColor playerColor, String opponent, ChessGame.TeamColor opponentColor) {

    public String toString() {
        return new Gson().toJson(this);
    }
}
