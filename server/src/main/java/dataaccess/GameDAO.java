package dataaccess;

import model.GameData;

import java.util.Collection;

public interface GameDAO {
    
    public GameData addGame(GameData game) throws DataAccessException;
    
    public GameData getGameData(int gameId) throws DataAccessException;

    public Collection<GameData> listGames() throws DataAccessException;

    public void updateGameData(GameData game) throws DataAccessException;
    
    public void deleteAllGames() throws DataAccessException;
    
}
