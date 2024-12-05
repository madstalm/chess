package service;

import dataaccess.GameDAO;
import dataaccess.InvalidInputException;
import model.GameData;
import model.AuthData;
import server.ListGamesResponse;
import chess.ChessGame;
import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;

import java.util.ArrayList;
import java.util.Collection;

public class GameService {
    private final GameDAO dataAccess;

    public GameService(GameDAO dataAccess) {
        this.dataAccess = dataAccess;
    }

    public Collection<ListGamesResponse> getGames() throws DataAccessException {
        Collection<GameData> games = dataAccess.listGames();
        Collection<ListGamesResponse> gamesList = new ArrayList<>();
        for (GameData game : games) {
            gamesList.add(new ListGamesResponse(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName()));
        }
        return gamesList;
    }

    /**
     * @param gameID
     * @return the ChessGame object for the given gameID
     * @throws DataAccessException
     */
    public ChessGame getGame(Integer gameID) throws DataAccessException {
        GameData game = dataAccess.getGameData(gameID);
        return game.game();
    }

    /**
     * @param gameID
     * @return the GameData object for the given gameID
     * @throws DataAccessException
     */
    public GameData getGameData(Integer gameID) throws DataAccessException {
        return dataAccess.getGameData(gameID);
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

    public void updateGame(Integer gameID, ChessGame game) throws DataAccessException {
        GameData updatedGame = getGameData(gameID);
        updatedGame = updatedGame.setGame(game);
        dataAccess.updateGameData(updatedGame);
    }

    public void updateGameData(GameData gameData) throws DataAccessException {
        dataAccess.updateGameData(gameData);
    }

    public void clear() throws DataAccessException {
        dataAccess.deleteAllGames();
    }

    
    
}
