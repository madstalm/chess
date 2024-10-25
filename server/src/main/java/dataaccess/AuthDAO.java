package dataaccess;

import model.AuthData;

public interface AuthDAO {

    public AuthData addAuthData(AuthData authData) throws Exception;

    public AuthData getAuthData(String authToken) throws Exception;

    public void deleteAuthData(String authToken) throws Exception;

    public void deleteAllAuthData() throws Exception;
    
}
