package dataaccess;

import model.AuthData;

public interface AuthDAO {

    public AuthData addAuthData(AuthData authData) throws DataAccessException;

    public AuthData getAuthData(String authToken) throws DataAccessException;

    public void deleteAuthData(String authToken) throws DataAccessException;

    public void deleteAllAuthData() throws DataAccessException;
    
}
