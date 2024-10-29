package dataaccess;
import java.sql.*;

import com.google.gson.Gson;

import model.UserData;

public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() throws Exception {
        DatabaseManager.configureDatabase();
    }

    public UserData addUserData(UserData userData) throws DataAccessException {
        var statement = "INSERT INTO userDB (username, userData) VALUES (?, ?)";
        var json = userData.toString();
        DatabaseManager.executeUpdate(statement, userData.username(), json);
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
        DatabaseManager.executeUpdate(statement);
    }

    private UserData fetchUserData(ResultSet rs) throws SQLException {
        var json = rs.getString("userData");
        var userData = new Gson().fromJson(json, UserData.class);
        return userData;
    }


}
