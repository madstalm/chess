package dataaccess;
import java.sql.*;

import com.google.gson.Gson;

import model.AuthData;

import static java.sql.Types.NULL;

public class SQLAuthDAO implements AuthDAO {
    
    public SQLAuthDAO() throws Exception {
        configureDatabase();
    }
    
    public AuthData addAuthData(AuthData authData) throws Exception {
        var statement = "INSERT INTO authDB (authToken, authData) VALUES (?, ?)";
        var json = new Gson().toJson(authData);
        executeAuthUpdate(statement, authData.authToken(), json);
        return authData;
    }

    public AuthData getAuthData(String authToken) throws Exception{
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, authData FROM authDB WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return fetchAuthData(rs);
                    }
                }
            }
        } catch (Exception ex) {
            throw new DataAccessException("Error: " + ex.getMessage());
        }
        return null;
    }

    public void deleteAuthData(String authToken) throws Exception {
        var statement = "DELETE FROM authDB WHERE authToken=?";
        executeAuthUpdate(statement, authToken);
    }

    public void deleteAllAuthData() throws Exception {
        var statement = "TRUNCATE authDB";
        executeAuthUpdate(statement);
    }

    private AuthData fetchAuthData(ResultSet rs) throws SQLException {
        //var token = rs.getInt("authToken");
        var json = rs.getString("json");
        var authData = new Gson().fromJson(json, AuthData.class);
        return authData;
    }

    private void executeAuthUpdate(String statement, Object... params) throws Exception {
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                for (var i = 0; i < params.length; i++) {
                    var param = params[i];
                    if (param instanceof String p) { ps.setString(i + 1, p); }
                    else if (param == null) { ps.setNull(i + 1, NULL); }
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error: " + ex.getMessage());
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  authDB (
              `authToken` varchar(256) NOT NULL,
              `authData` TEXT DEFAULT NULL,
              PRIMARY KEY (`authToken`)
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
