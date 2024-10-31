package dataaccess;
import java.sql.*;

import com.google.gson.Gson;

import model.AuthData;

public class SQLAuthDAO implements AuthDAO {
    
    public SQLAuthDAO() throws Exception {
        DatabaseManager.configureDatabase();
    }
    
    public AuthData addAuthData(AuthData authData) throws Exception {
        var statement = "INSERT INTO authDB (authToken, authData) VALUES (?, ?)";
        var json = authData.toString();
        DatabaseManager.executeUpdate(statement, authData.authToken(), json);
        return authData;
    }

    public AuthData getAuthData(String authToken) throws Exception {
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
        DatabaseManager.executeUpdate(statement, authToken);
    }

    public void deleteAllAuthData() throws Exception {
        var statement = "TRUNCATE authDB";
        DatabaseManager.executeUpdate(statement);
    }

    private AuthData fetchAuthData(ResultSet rs) throws SQLException {
        //var token = rs.getInt("authToken");
        var json = rs.getString("authData");
        var authData = new Gson().fromJson(json, AuthData.class);
        return authData;
    }

}
