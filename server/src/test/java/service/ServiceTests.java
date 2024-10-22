package service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dataaccess.DataAccessException;
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
    
}
