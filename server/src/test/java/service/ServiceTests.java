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
import dataaccess.AlreadyTakenException;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.AuthService;
import service.GameService;
import service.UserService;
import org.mindrot.jbcrypt.BCrypt;

import chess.ChessGame;

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
        AuthData authorization = new AuthData("123","bob");
        authDAO.addAuthData(authorization);
        authService.clear();
        gameService.clear();
        userService.clear();
        Assertions.assertEquals(null, authDAO.getAuthData("123"),
                "authDao was not cleared");
    }
    
    @Test
    @DisplayName("registerUser() positive")
    public void registerUserService() throws Exception {
        UserData user = new UserData("bob", "theBuilder", "he@canfix.it");
        UserData testReturn = userService.registerUser(user);
        Assertions.assertEquals(user.username(), testReturn.username(), 
                "Response did not give the same username as user");
        Assertions.assertEquals(userDAO.getUserData("bob").email(), "he@canfix.it", 
                "password was not recorded in Memory");
    }

    @Test
    @DisplayName("registerUser() negative")
    public void registerUserServiceN() throws Exception {
        UserData user = new UserData("bob", null, "hecanfix.it");
        assertThrows(InvalidInputException.class,
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
    public void createAuthServiceN() throws Exception {
        UserData user = new UserData(null, "theBuilder", "hecanfix.it");
        assertThrows(InvalidInputException.class,
            () -> authService.createAuth(user));
    }

    @Test
    @DisplayName("checkLogin() positive")
    public void checkLoginService() throws Exception {
        UserData user = new UserData("bob", BCrypt.hashpw("theBuilder", BCrypt.gensalt()), "he@canfix.it");
        userDAO.addUserData(user);
        UserData loginUser = new UserData("bob", "theBuilder", null);
        UserData checked = userService.checkLogin(loginUser);
        Assertions.assertEquals(checked, userDAO.getUserData("bob"), 
                "did not return the same user");
    }

    @Test
    @DisplayName("checkLogin() negative")
    public void checkLoginServiceN() throws Exception {
        UserData user = new UserData("bob", BCrypt.hashpw("theBuilder", BCrypt.gensalt()), "he@canfix.it");
        userDAO.addUserData(user);
        UserData loginUser = new UserData("bob", "thebuilder", null);
        assertThrows(UnauthorizedException.class,
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
    public void checkLogoutServiceN() throws Exception {
        UserData user = new UserData("bob", "theBuilder", "he@canfix.it");
        userDAO.addUserData(user);
        authService.createAuth(user);
        assertThrows(UnauthorizedException.class,
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
        AuthData checked = authService.checkAuth("123456");
        Assertions.assertEquals(authorization, checked,
                "authorization was not returned by checkAuth()");
    }

    @Test
    @DisplayName("checkAuth() negative")
    public void checkAuthServiceN() throws Exception {
        AuthData authorization = new AuthData("123456", "bob");
        authDAO.addAuthData(authorization);
        assertThrows(UnauthorizedException.class,
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
    public void getGamesServiceN() throws Exception {
        Collection<GameData> empty = new ArrayList<>();
        Assertions.assertIterableEquals(empty, gameService.getGames(),
                "getGames() returned games");
    }

    @Test
    @DisplayName("createGame() positive")
    public void createGameService() throws Exception {
        GameData game = new GameData(null, null, null, "a game", null);
        Integer gameID = gameService.gameCreator(game);
        Assertions.assertEquals(1, gameID, "game creation did not start at 1");
        Assertions.assertNotNull(gameDAO.getGameData(1), "there is no game with gameID=1");
    }

    @Test
    @DisplayName("createGame() negative")
    public void createGameServiceN() throws Exception {
        GameData game = new GameData(null, null, null, null, null);
        assertThrows(DataAccessException.class,
            () -> gameService.gameCreator(game));
        assertThrows(DataAccessException.class,
            () -> gameService.gameCreator(null));
    }

    @Test
    @DisplayName("joinGame() positive")
    public void joinGameService() throws Exception {
        GameData game = new GameData(null, null, null, "a game", null);
        Integer gameID = gameService.gameCreator(game);
        UserData user = new UserData("bob", "theBuilder", "he@canfix.it");
        userDAO.addUserData(user);
        AuthData authorization = new AuthData("123456", "bob");
        authDAO.addAuthData(authorization);
        gameService.gameJoiner(authorization, gameID, ChessGame.TeamColor.BLACK);
        Assertions.assertEquals(user.username(), gameDAO.getGameData(gameID).blackUsername(),
                "game creation did not start at 1");
    }

    @Test
    @DisplayName("joinGame() negative")
    public void joinGameServiceN() throws Exception {
        GameData game = new GameData(null, null, "scoop", "a game", null);
        Integer gameID = gameService.gameCreator(game);
        UserData user = new UserData("bob", "theBuilder", "he@canfix.it");
        userDAO.addUserData(user);
        AuthData authorization = new AuthData("123456", "bob");
        authDAO.addAuthData(authorization);
        assertThrows(AlreadyTakenException.class,
            () -> gameService.gameJoiner(authorization, gameID, ChessGame.TeamColor.BLACK));
    }

}
