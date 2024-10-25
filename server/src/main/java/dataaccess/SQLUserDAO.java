package dataaccess;
import java.sql.*;

import com.google.gson.Gson;

import model.UserData;

import static java.sql.Types.NULL;

public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() throws Exception {
        configureDatabase();
    }

    public UserData addUserData(UserData userData) throws DataAccessException {
        var statement = "INSERT INTO userDB (username, userData) VALUES (?, ?)";
        var json = userData.toString();
        executeUserUpdate(statement, userData.username(), json);
        return userData;
    }

    public UserData getUserData(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, userData FROM userDB WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return fetchUserData(rs);
                    }
                }
            }
        } catch (Exception ex) {
            throw new DataAccessException("Error: " + ex.getMessage());
        }
        return null;
    }

    public void deleteAllUsers() throws DataAccessException {
        var statement = "TRUNCATE userDB";
        executeUserUpdate(statement);
    }

    private UserData fetchUserData(ResultSet rs) throws SQLException {
        var json = rs.getString("userData");
        var userData = new Gson().fromJson(json, UserData.class);
        return userData;
    }

    private void executeUserUpdate(String statement, Object... params) throws DataAccessException {
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
            CREATE TABLE IF NOT EXISTS  userDB (
              `username` varchar(256) NOT NULL,
              `userData` TEXT NOT NULL,
              PRIMARY KEY (`username`)
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
