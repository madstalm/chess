package service;

import dataaccess.AuthDAO;
import model.AuthData;
import model.UserData;
import dataaccess.DataAccessException;
import dataaccess.InvalidInputException;
import dataaccess.UnauthorizedException;

import java.util.Collection;
import java.util.Random;

public class AuthService {
    private final AuthDAO dataAccess;
    private Integer modifier;

    public AuthService(AuthDAO dataAccess) {
        this.dataAccess = dataAccess;
        this.modifier = 0;
    }

    public AuthData createAuth(UserData userData) throws Exception {
        if ((userData.username() == null) || (userData.username().isEmpty())) {
            throw new InvalidInputException("Error: no username passed");
        }
        String username = userData.username();
        String authToken = createToken(username);
        AuthData authData = new AuthData(authToken, username);
        dataAccess.addAuthData(authData);
        return authData;
    }

    public AuthData checkAuth(String authToken) throws Exception {
        AuthData authorization = dataAccess.getAuthData(authToken);
        if (authorization == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        return authorization;
    }

    public void checkLogout(String authToken) throws Exception {
        checkAuth(authToken);
        dataAccess.deleteAuthData(authToken);
    }

    public void clear() throws Exception {
        dataAccess.deleteAllAuthData();
        modifier = 0;
    }

    private String createToken(String username) {
        long seed = System.currentTimeMillis() + username.hashCode();
        Random random = new Random(seed);
        ++modifier;
        return String.valueOf(random.nextInt(Integer.MAX_VALUE) + modifier);
    }

}
