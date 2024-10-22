package service;

import dataaccess.GameDAO;
import model.GameData;
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

    public void clear() throws DataAccessException {
        dataAccess.deleteAllGames();
    }
    
}
