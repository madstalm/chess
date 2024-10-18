package service;

import dataaccess.UserDAO;
import model.UserData;
import dataaccess.DataAccessException;

import java.util.Collection;

public class UserService {
    private final UserDAO dataAccess;

    public UserService(UserDAO dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clear() throws DataAccessException {
        dataAccess.deleteAllUsers();
    }
    
}
