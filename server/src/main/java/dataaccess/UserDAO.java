package dataaccess;

import model.UserData;

public interface UserDAO {
    
    public UserData addUserData(UserData userData) throws DataAccessException;
    
    public UserData getUserData(String username) throws DataAccessException;

    public void deleteAllUsers() throws DataAccessException;

}
