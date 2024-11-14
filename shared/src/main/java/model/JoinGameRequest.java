package model;

import chess.ChessGame;

public record JoinGameRequest(ChessGame.TeamColor playerColor, Integer gameID) {

    public JoinGameRequest setPlayerColor(ChessGame.TeamColor playerColor) {
        return new JoinGameRequest(playerColor, this.gameID);
    }
}
