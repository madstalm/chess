package service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dataaccess.DataAccessException;
import dataaccess.InvalidInputException;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.AuthService;
import service.GameService;
import service.UserService;

public class ServiceTests {
    private static dataaccess.AuthDAO authDAO;
    private static dataaccess.GameDAO gameDAO;
    private static dataaccess.UserDAO userDAO;
    private static AuthService authService;
    private static GameService gameService;
    private static UserService userService;

    @BeforeAll
    public static void init() {
        authDAO = new dataaccess.MemoryAuthDAO();
        gameDAO = new dataaccess.MemoryGameDAO();
        userDAO = new dataaccess.MemoryUserDAO();
        authService = new AuthService(authDAO);
        gameService = new GameService(gameDAO);
        userService = new UserService(userDAO);
    }

    @BeforeEach
    public void setupService() {
        authService = new AuthService(authDAO);
        gameService = new GameService(gameDAO);
        userService = new UserService(userDAO);
    }

    @Test
    @DisplayName("Clear Positive")
    public void clearService() throws Exception {
        authService.clear();
        gameService.clear();
        userService.clear();
    }
    
    @Test
    @DisplayName("registerUser() positive")
    public void registerUserService() throws Exception {
        UserData user = new UserData("bob", "theBuilder", "he@canfix.it");
        UserData testReturn = userService.registerUser(user);
        Assertions.assertEquals(user.username(), testReturn.username(), 
                "Response did not give the same username as user");
        Assertions.assertEquals(userDAO.getUserData("bob").password(), "theBuilder", 
                "password was not recorded in Memory");
    }

    @Test
    @DisplayName("registerUser() negative")
    public void registerUserService_N() throws Exception {
        UserData user = new UserData("bob", "theBuilder", "hecanfix.it");
        InvalidInputException thrown = assertThrows(InvalidInputException.class,
            () -> userService.registerUser(user));
    }

    @Test
    @DisplayName("createAuth() positive")
    public void createAuthService() throws Exception {
        UserData user = new UserData("bob", "theBuilder", "hecanfix.it");
        AuthData authorization = authService.createAuth(user);
        Assertions.assertNotNull(authorization.authToken(), 
                "Response did not return authentication String");
    }

    @Test
    @DisplayName("createAuth() negative")
    public void createAuthService_N() throws Exception {
        UserData user = new UserData(null, "theBuilder", "hecanfix.it");
        InvalidInputException thrown = assertThrows(InvalidInputException.class,
            () -> authService.createAuth(user));
    }

    
}
