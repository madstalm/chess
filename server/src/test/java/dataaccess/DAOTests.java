package dataaccess;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dataaccess.DataAccessException;
import dataaccess.InvalidInputException;
import dataaccess.UnauthorizedException;
import dataaccess.AlreadyTakenException;
import model.AuthData;
import model.GameData;
import model.UserData;
import chess.ChessGame;

public class DAOTests {
    private static dataaccess.AuthDAO authDAO;
    private static dataaccess.GameDAO gameDAO;
    private static dataaccess.UserDAO userDAO;

    @BeforeAll
    public static void init() throws Exception {
        authDAO = new dataaccess.SQLAuthDAO();
        gameDAO = new dataaccess.SQLGameDAO();
        userDAO = new dataaccess.SQLUserDAO();
    }

    @BeforeEach
    public void setupService() throws Exception {
        authDAO.deleteAllAuthData();
        gameDAO.deleteAllGames();
        userDAO.deleteAllUsers();
    }

    @Test
    @DisplayName("Clear Positive")
    public void clearDAO() throws Exception {
        AuthData authorization = new AuthData("123","bob");
        UserData user = new UserData("bob", "thebuilder", "bob@bob.com");
        GameData game = new GameData(null, null, null, "hecanfixit", null);
        authDAO.addAuthData(authorization);
        gameDAO.addGame(game);
        userDAO.addUserData(user);
        authDAO.deleteAllAuthData();
        gameDAO.deleteAllGames();
        userDAO.deleteAllUsers();
        Assertions.assertEquals(null, authDAO.getAuthData("123"),
                "authDao was not cleared");
        Assertions.assertEquals(null, gameDAO.getGameData(1),
                "gameDao was not cleared");
        Assertions.assertEquals(null, userDAO.getUserData("bob"),
                "userDao was not cleared");
    }

    @Test
    @DisplayName("addAuth Positive")
    public void addAuth() throws Exception {
        AuthData authorization = new AuthData("123","bob");
        authDAO.addAuthData(authorization);
        Assertions.assertEquals(authorization, authDAO.getAuthData("123"),
                "authorization not added to DB");
    }

    @Test
    @DisplayName("addAuth Negative")
    public void addAuthN() throws Exception {
        AuthData authorization = new AuthData(null,"bob");
        assertThrows(Exception.class,
            () -> authDAO.addAuthData(authorization));
    }

    @Test
    @DisplayName("getAuth Positive")
    public void getAuth() throws Exception {
        AuthData authorization = new AuthData("123","bob");
        AuthData authorizationII = new AuthData("456","jill");
        authDAO.addAuthData(authorization);
        authDAO.addAuthData(authorizationII);
        Assertions.assertEquals(authorizationII, authDAO.getAuthData("456"),
                "authDao retrieved wrong authorization");
        Assertions.assertEquals(authorization, authDAO.getAuthData("123"),
                "authDao retrieved wrong authorization");    
    }

    @Test
    @DisplayName("getAuth Negative")
    public void getAuthN() throws Exception {
        AuthData authorization = new AuthData("123","bob");
        authDAO.addAuthData(authorization);
        Assertions.assertEquals(null, authDAO.getAuthData("789"),
                "authDao retrieved authorization that shouldn't exist");
    }

    @Test
    @DisplayName("deleteAuth Positive")
    public void deleteAuth() throws Exception {
        AuthData authorization = new AuthData("123","bob");
        AuthData authorizationII = new AuthData("456","jill");
        authDAO.addAuthData(authorization);
        authDAO.addAuthData(authorizationII);
        Assertions.assertEquals(authorizationII, authDAO.getAuthData("456"),
                "authDao retrieved wrong authorization");
        Assertions.assertEquals(authorization, authDAO.getAuthData("123"),
                "authDao retrieved wrong authorization");
        authDAO.deleteAuthData("123");
        Assertions.assertEquals(null, authDAO.getAuthData("123"),
                "authDao retrieved authorization that shouldn't exist");
        Assertions.assertEquals(authorizationII, authDAO.getAuthData("456"),
                "authDao retrieved wrong authorization");
    }

    @Test
    @DisplayName("addUser Positive")
    public void addUser() throws Exception {
        UserData user = new UserData("bob", "thebuilder", "bob@bob.com");
        userDAO.addUserData(user);
        Assertions.assertEquals(user, userDAO.getUserData("bob"),
                "user not added to DB");
    }

    @Test
    @DisplayName("addUser Negative")
    public void addUserN() throws Exception {
        UserData user = new UserData(null, "thebuilder", "bob@bob.com");
        assertThrows(Exception.class,
            () -> userDAO.addUserData(user));
    }
    
    @Test
    @DisplayName("getUser Positive")
    public void getUser() throws Exception {
        UserData user = new UserData("bob", "thebuilder", "bob@bob.com");
        UserData userII = new UserData("bill", "imjusta", "sittin@capitol.hill");
        userDAO.addUserData(user);
        userDAO.addUserData(userII);
        Assertions.assertEquals(userII, userDAO.getUserData("bill"),
                "userDao retrieved wrong user");
        Assertions.assertEquals(user, userDAO.getUserData("bob"),
                "userDao retrieved wrong user");    
    }

    @Test
    @DisplayName("getUser Negative")
    public void getUserN() throws Exception {
        UserData user = new UserData("bob", "thebuilder", "bob@bob.com");
        userDAO.addUserData(user);
        Assertions.assertEquals(null, userDAO.getUserData("bill"),
                "userDao retrieved userData that shouldn't exist");
    }
}
