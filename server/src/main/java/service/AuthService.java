package service;

import dataaccess.AuthDAO;
import model.AuthData;
import dataaccess.DataAccessException;

import java.util.Collection;

public class AuthService {
    private final AuthDAO dataAccess;

    public AuthService(AuthDAO dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clear() throws DataAccessException {
        dataAccess.deleteAllAuthData();
    }

}
