package dataaccess;

import java.util.Collection;
import java.util.HashMap;

import model.GameData;

public class MemoryGameDAO {
    private int nextId = 1;
    
    final private HashMap<Integer, GameData> games = new HashMap<>();

    public GameData addGame(GameData game) {
        nextId++;
        
        game = new GameData(nextId, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game());
        games.put(game.gameID(), game);
        return game;
    }
    
    public GameData getGameData(int gameId) {
        return games.get(gameId);
    }

    public Collection<GameData> listGames() {
        return games.values();
    }

    ublic void deleteGame(int gameId) {
        games.remove(gameId);
    }

    public void deleteAllGames() {
        games.clear();
        nextId = 1; //resets the game IDs to 1
    }
    
}
