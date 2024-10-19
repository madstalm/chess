package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {
    final private HashMap<String, UserData> users = new HashMap<>();

    public UserData addUserData(UserData userData) {
        users.put(userData.username(), userData);
        return userData;
    }

    public UserData getUserData(String username) {
        return users.get(username);
    }

    public void deleteAllUsers() {
        users.clear();
    }

}
