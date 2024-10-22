package service;

import dataaccess.AuthDAO;
import model.AuthData;
import model.UserData;
import dataaccess.DataAccessException;

import java.util.Collection;
import java.util.Random;

public class AuthService {
    private final AuthDAO dataAccess;

    public AuthService(AuthDAO dataAccess) {
        this.dataAccess = dataAccess;
    }

    public AuthData createAuth(UserData userData) throws Exception {
        String username = userData.username();
        String authToken = createToken(username);
        AuthData authData = new AuthData(authToken, username);
        dataAccess.addAuthData(authData);
        return authData;
    }

    public void clear() throws DataAccessException {
        dataAccess.deleteAllAuthData();
    }

    private String createToken(String username) {
        long seed = System.currentTimeMillis() + username.hashCode();
        Random random = new Random(seed);
        return String.valueOf(random.nextInt(Integer.MAX_VALUE));
    }

}
