package service;

import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import model.AuthData;
import model.UserData;
import dataaccess.DataAccessException;
import dataaccess.AlreadyTakenException;

import java.util.Collection;

public class UserService {
    private final UserDAO dataAccess;

    public UserService(UserDAO dataAccess) {
        this.dataAccess = dataAccess;
    }

    public UserData registerUser(UserData user) throws Exception {
        /*Steps
        1. Check if the user already exists
        2. If not, create the user and add their info to the db
        3. get authentication info for the new user
        */
        String username = user.username();
        if (dataAccess.getUserData(username) != null) {
            throw new AlreadyTakenException("Error: already taken");
        }
        dataAccess.addUserData(user);
        return user;
    }

    //public AuthData login(UserData user) {}

	public void logout(AuthData auth) {}

    public void clear() throws DataAccessException {
        dataAccess.deleteAllUsers();
    }
    
}
