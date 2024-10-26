package dataaccess;
import java.sql.*;

import com.google.gson.Gson;

import chess.ChessGame;
import model.AuthData;
import model.GameData;

import static java.sql.Types.NULL;

public class SQLGameDAO implements GameDAO {

    public SQLGameDAO() throws DataAccessException {
        configureDatabase();
    }

    public GameData addGame(GameData game) throws DataAccessException {
        var statement = "INSERT INTO gameDB (gameId, gameData) VALUES (?, ?)";
        game = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), new ChessGame())
        var gameId = executeGameUpdate(statement, null, game);
        return game.setGameID(gameId);
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
        var result = new ArrayList<Pet>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, json FROM pet";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(readPet(rs));
                    }
                }
            }
        } catch (Exception e) {
            throw new ResponseException(500, String.format("Unable to read data: %s", e.getMessage()));
        }
        return result;
    }

    public void updateGameData(GameData game) throws DataAccessException {
        games.put(game.gameID(), game);
    }

    public void deleteGame(int gameId) throws DataAccessException {
        var statement = "DELETE FROM gameDB WHERE gameId=?";
        executeGameUpdate(statement, gameId);
    }

    public void deleteAllGames() throws DataAccessException {
        var statement = "TRUNCATE gameDB";
        executeGameUpdate(statement);
    }

    private GameData fetchGameData(ResultSet rs) throws SQLException {
        //var token = rs.getInt("authToken");
        var json = rs.getString("authData");
        var gameData = new Gson().fromJson(json, GameData.class);
        return gameData;
    }

    private int executeGameUpdate(String statement, Object... params) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) { ps.setString(i + 1, p); }
                    else if (param instanceof Integer p) { ps.setInt(i + 1, p); }
                    else if (param == null) { ps.setNull(i + 1, NULL); }
                }

            var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: " + ex.getMessage());
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  gameDB (
              `gameId` int NOT NULL AUTO_INCREMENT,
              `gameData` TEXT DEFAULT NULL,
              PRIMARY KEY (`gameId`)
            )
            """
    };


    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: " + ex.getMessage());
        }
    }
}
