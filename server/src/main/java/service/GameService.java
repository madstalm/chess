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

    public void clear() throws DataAccessException {
        dataAccess.deleteAllGames();
    }
    
}
