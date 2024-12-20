package dataaccess;

import java.util.Collection;
import java.util.HashMap;

import model.GameData;
import chess.ChessGame;

public class MemoryGameDAO implements GameDAO {
    private int nextId = 0;
    
    final private HashMap<Integer, GameData> games = new HashMap<>();

    public GameData addGame(GameData game) throws DataAccessException {
        nextId++;

        if ((game == null)||(game.gameName() == null)) {
            throw new DataAccessException("Error: invalid game was passed to DAO");
        }
        
        game = new GameData(nextId, game.whiteUsername(), game.blackUsername(), game.gameName(), new ChessGame());
        games.put(game.gameID(), game);
        return game;
    }
    
    public GameData getGameData(int gameId) {
        return games.get(gameId);
    }

    public Collection<GameData> listGames() {
        return games.values();
    }

    public void updateGameData(GameData game) throws DataAccessException {
        if ((game == null)||(game.gameName() == null)||(getGameData(game.gameID()) == null)) {
            throw new DataAccessException("Error: invalid game was passed to DAO");
        }
        games.put(game.gameID(), game);
    }

    public void deleteAllGames() {
        games.clear();
        nextId = 0; //resets the game IDs to 1
    }
    
}
