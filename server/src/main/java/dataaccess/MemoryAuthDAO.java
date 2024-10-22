package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    final private HashMap<String, AuthData> authentications = new HashMap<>();

    public AuthData addAuthData(AuthData authData) {
        authentications.put(authData.authToken(), authData);
        return authData;
    }

    public AuthData getAuthData(String authToken) {
        return authentications.get(authToken);
    }

    public void deleteAuthData(String authToken) {
        authentications.remove(authToken);
    }

    public void deleteAllAuthData() {
        authentications.clear();
    }

}