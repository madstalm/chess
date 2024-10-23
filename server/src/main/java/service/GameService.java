package service;

import dataaccess.GameDAO;
import dataaccess.InvalidInputException;
import model.GameData;
import model.AuthData;
import chess.ChessGame;
import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;

import java.util.Collection;

public class GameService {
    private final GameDAO dataAccess;

    public GameService(GameDAO dataAccess) {
        this.dataAccess = dataAccess;
    }

    public Collection<GameData> getGames() throws DataAccessException {
        return dataAccess.listGames();
    }

    public Integer gameCreator(GameData game) throws DataAccessException {
        game = dataAccess.addGame(game);
        return game.gameID();
    }

    public void gameJoiner(AuthData authorization, Integer gameID, ChessGame.TeamColor playerColor) throws Exception {
        String username = authorization.username();
        if ((gameID == null)||(!(playerColor == ChessGame.TeamColor.BLACK)&&!(playerColor == ChessGame.TeamColor.WHITE))) {
            throw new InvalidInputException("Error: bad request");
        }
        GameData game = dataAccess.getGameData(gameID);
        if (game == null) {
            throw new DataAccessException("Error: invalid gameID");
        }
        GameData updatedGame = null;
        switch (playerColor) {
            case WHITE:
                if (game.whiteUsername() != null) {
                    throw new AlreadyTakenException("Error: already taken");
                }
                updatedGame = game.setWhitePlayer(username);
            break;
            case BLACK:
                if (game.blackUsername() != null) {
                    throw new AlreadyTakenException("Error: already taken");
                }
                updatedGame = game.setBlackPlayer(username);
            break;
        }
        dataAccess.updateGameData(updatedGame);
    }

    public void clear() throws DataAccessException {
        dataAccess.deleteAllGames();
    }
    
}
