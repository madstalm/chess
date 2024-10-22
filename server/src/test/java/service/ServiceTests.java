package service;

import java.util.*;

import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dataaccess.DataAccessException;
import dataaccess.InvalidInputException;
import dataaccess.UnauthorizedException;
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
    public void setupService() throws Exception {
        authDAO.deleteAllAuthData();
        gameDAO.deleteAllGames();
        userDAO.deleteAllUsers();
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
        UserData user = new UserData("bob", "theBuilder", "he@canfix.it");
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

    @Test
    @DisplayName("checkLogin() positive")
    public void checkLoginService() throws Exception {
        UserData user = new UserData("bob", "theBuilder", "he@canfix.it");
        userDAO.addUserData(user);
        UserData loginUser = new UserData("bob", "theBuilder", null);
        UserData checked = userService.checkLogin(loginUser);
        Assertions.assertEquals(user.password(), checked.password(), 
                "did not return the same user");
    }

    @Test
    @DisplayName("checkLogin() negative")
    public void checkLoginService_N() throws Exception {
        UserData user = new UserData("bob", "theBuilder", "he@canfix.it");
        userDAO.addUserData(user);
        UserData loginUser = new UserData("bob", "thebuilder", null);
        UnauthorizedException thrown = assertThrows(UnauthorizedException.class,
            () -> userService.checkLogin(loginUser));
    }

    @Test
    @DisplayName("checkLogout() positive")
    public void checkLogoutService() throws Exception {
        UserData user = new UserData("bob", "theBuilder", "he@canfix.it");
        userDAO.addUserData(user);
        AuthData authorization = authService.createAuth(user);
        Assertions.assertEquals(authorization, authDAO.getAuthData(authorization.authToken()),
                "authorization was not added to Memory");
        authService.checkLogout(authorization.authToken());
        Assertions.assertEquals(null, authDAO.getAuthData(authorization.authToken()),
                "authorization was not removed from Memory");
    }

    @Test
    @DisplayName("checkLogout() negative")
    public void checkLogoutService_N() throws Exception {
        UserData user = new UserData("bob", "theBuilder", "he@canfix.it");
        userDAO.addUserData(user);
        authService.createAuth(user);
        UnauthorizedException thrown = assertThrows(UnauthorizedException.class,
            () -> authService.checkLogout("abcdefg"));
    }

    @Test
    @DisplayName("login and logout and login")
    public void loginlogoutloginService() throws Exception {
        UserData user = new UserData("bob", "theBuilder", "he@canfix.it");
        userDAO.addUserData(user);
        AuthData authorization = authService.createAuth(user); //first login
        Assertions.assertEquals(authorization, authDAO.getAuthData(authorization.authToken()),
                "authorization was not added to Memory");
        authService.checkLogout(authorization.authToken()); //first logout
        Assertions.assertEquals(null, authDAO.getAuthData(authorization.authToken()),
                "authorization was not removed from Memory"); //make sure logout removed authdata from memory
        Assertions.assertEquals(user.password(), userDAO.getUserData("bob").password(),
                "user was deleted prematurely"); //make sure user is still in memory
        AuthData authorization2 = authService.createAuth(user); //2nd login
        Assertions.assertEquals(authorization2.authToken(), authDAO.getAuthData(authorization2.authToken()).authToken(),
                "authToken not in memory"); //check that new authdata was created
    }

    @Test
    @DisplayName("checkAuth() positive")
    public void checkAuthService() throws Exception {
        AuthData authorization = new AuthData("123456", "bob");
        authDAO.addAuthData(authorization);
        String checked = authService.checkAuth("123456");
        Assertions.assertEquals(authorization.authToken(), checked,
                "authorization was not returned by checkAuth()");
    }

    @Test
    @DisplayName("checkAuth() negative")
    public void checkAuthService_N() throws Exception {
        AuthData authorization = new AuthData("123456", "bob");
        authDAO.addAuthData(authorization);
        UnauthorizedException thrown = assertThrows(UnauthorizedException.class,
            () -> authService.checkAuth("23456"));
    }

    @Test
    @DisplayName("getGames() positive")
    public void getGamesService() throws Exception {
        GameData game = new GameData(null, null, null, "a game", new chess.ChessGame());
        gameDAO.addGame(game);
        Assertions.assertNotNull(gameService.getGames(),
                "getGames() returned no games");
    }

    @Test
    @DisplayName("getGames() negative")
    public void getGamesService_N() throws Exception {
        Collection<GameData> empty = new ArrayList<>();
        Assertions.assertIterableEquals(empty, gameService.getGames(),
                "getGames() returned games");
    }

    @Test
    @DisplayName("createGame() positive")
    public void createGameService() throws Exception {
        GameData game = new GameData(null, null, null, "a game", new chess.ChessGame());
        Integer gameID = gameService.gameCreator(game);
        Assertions.assertEquals(1, gameID, "game creation did not start at 1");
        Assertions.assertNotNull(gameDAO.getGameData(1), "there is no game with gameID=1");
    }

    @Test
    @DisplayName("createGame() negative")
    public void createGameService_N() throws Exception {
        GameData game = new GameData(null, null, null, null, null);
        assertThrows(DataAccessException.class,
            () -> gameService.gameCreator(game));
        assertThrows(DataAccessException.class,
            () -> gameService.gameCreator(null));
    }

}
