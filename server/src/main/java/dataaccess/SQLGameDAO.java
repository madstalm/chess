package dataaccess;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

import com.google.gson.Gson;

import chess.ChessGame;
import model.GameData;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() throws DataAccessException {
        DatabaseManager.configureDatabase();
    }

    public GameData addGame(GameData game) throws DataAccessException {
        var statement = "INSERT INTO gameDB (gameData) VALUES (?)";
        if ((game == null)||(game.gameName() == null)) {
            throw new DataAccessException("Error: invalid game was passed to DAO");
        }
        game = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), new ChessGame());
        var gameId = DatabaseManager.executeUpdate(statement, new Gson().toJson(game));
        game = game.setGameID(gameId);
        updateGameData(game);
        return game;
    }
    
    public GameData getGameData(int gameId) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, gameData FROM gameDB WHERE gameId=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameId);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return fetchGameData(rs);
                    }
                }
            }
        } catch (Exception ex) {
            throw new DataAccessException("Error: " + ex.getMessage());
        }
        return null;
    }

    public Collection<GameData> listGames() throws DataAccessException {
        var result = new ArrayList<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, gameData FROM gameDB";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(fetchGameData(rs));
                    }
                }
            }
        } catch (Exception ex) {
            throw new DataAccessException("Error: " + ex.getMessage());
        }
        return result;
    }

    public void updateGameData(GameData game) throws DataAccessException {
        if ((game == null)||(game.gameName() == null)||(getGameData(game.gameID()) == null)) {
            throw new DataAccessException("Error: invalid game was passed to DAO");
        }
        var statement = "UPDATE gameDB SET gameData=? WHERE gameId=?";
        DatabaseManager.executeUpdate(statement, new Gson().toJson(game), game.gameID());
    }

    public void deleteAllGames() throws DataAccessException {
        var statement = "TRUNCATE gameDB";
        DatabaseManager.executeUpdate(statement);
    }

    private GameData fetchGameData(ResultSet rs) throws SQLException {
        var json = rs.getString("gameData");
        var gameData = new Gson().fromJson(json, GameData.class);
        return gameData;
    }

}
