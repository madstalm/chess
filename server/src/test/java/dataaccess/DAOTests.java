package dataaccess;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;

import dataaccess.DataAccessException;
import dataaccess.InvalidInputException;
import dataaccess.UnauthorizedException;
import dataaccess.AlreadyTakenException;
import model.AuthData;
import model.GameData;
import model.UserData;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

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

    @Test
    @DisplayName("addGameData")
    public void addGameData() throws Exception {
        GameData game = new GameData(null, null, null, "the Game", null);
        int setID = gameDAO.addGame(game).gameID();
        ChessGame chess = new ChessGame();
        Assertions.assertEquals(chess, gameDAO.getGameData(setID).game(),
                "chessgame instance not created when game added");
        GameData gameII = new GameData(null, null, null, "a Game", null);
        setID = gameDAO.addGame(gameII).gameID();
        Assertions.assertEquals(2, setID, 
                "gameID not iterating");
    }

    @Test
    @DisplayName("addGame Negative")
    public void addGameDataN() throws Exception {
        GameData game = new GameData(null, null, null, null, null);
        assertThrows(DataAccessException.class,
            () -> gameDAO.addGame(game));
        assertThrows(DataAccessException.class,
            () -> gameDAO.addGame(null));
    }

    @Test
    @DisplayName("getGameData Positive")
    public void getGame() throws Exception {
        GameData game = new GameData(null, null, null, "the Game", null);
        GameData gameII = new GameData(null, null, null, "a Game", null);
        game = gameDAO.addGame(game);
        gameII = gameDAO.addGame(gameII);
        Assertions.assertEquals(game, gameDAO.getGameData(game.gameID()),
                "gameDao retrieved wrong game");   
        Assertions.assertEquals(gameII, gameDAO.getGameData(gameII.gameID()),
                "gameDao retrieved wrong game");
    }

    @Test
    @DisplayName("getGameData Negative")
    public void getGameN() throws Exception {
        GameData game = new GameData(null, null, null, "the Game", null);
        game = gameDAO.addGame(game);
        Assertions.assertEquals(null, gameDAO.getGameData(2),
                "gameDao retrieved gameData that shouldn't exist");
        Assertions.assertEquals(game, gameDAO.getGameData(game.gameID()),
                "gameDao retrieved wrong game"); 
    }

    @Test
    @DisplayName("listGames Positive")
    public void listGameData() throws Exception {
        GameData game = new GameData(null, null, null, "the Game", null);
        GameData gameII = new GameData(null, null, null, "a Game", null);
        Assertions.assertEquals(new ArrayList<GameData>(), gameDAO.listGames(),
                "gameDao shouldn't have listed any games");
        game = gameDAO.addGame(game);
        gameII = gameDAO.addGame(gameII);
        ArrayList<GameData> games = new ArrayList<>(Arrays.asList(game, gameII));
        Assertions.assertEquals(games, gameDAO.listGames(),
                "gameDao didn't list games as expected");
    }

    @Test
    @DisplayName("listGames Negative")
    public void listGameDataN() throws Exception {
        GameData game = new GameData(null, null, null, "the Game", null);
        GameData gameII = new GameData(null, null, null, "a Game", null);
        gameDAO.addGame(game);
        gameDAO.addGame(gameII);
        ArrayList<GameData> games = new ArrayList<>(Arrays.asList(game, gameII));
        Assertions.assertNotEquals(games, gameDAO.listGames(),
                "games listed were not updated when created");
    }

    @Test
    @DisplayName("updateGameData Positive")
    public void updateGame() throws Exception {
        GameData game = new GameData(null, null, null, "the Game", null);
        game = gameDAO.addGame(game);
        game = game.setWhitePlayer("bob");
        gameDAO.updateGameData(game);
        Assertions.assertEquals(game, gameDAO.getGameData(game.gameID()),
                "gameDao didn't update as expected");
    }

    @Test
    @DisplayName("updateGameData Negative")
    public void updateGameN() throws Exception {
        GameData game = new GameData(null, null, null, "the Game", null);
        GameData gameWithID = gameDAO.addGame(game);
        GameData gameWithBadID = gameWithID.setGameID(3);
        assertThrows(Exception.class,
            () -> gameDAO.updateGameData(gameWithBadID));
    }

    //no tests for delete game because while method exists, it is never called (at least thus far)

    @Test
    @DisplayName("change game board updateGameData")
    public void updateGameBoard() throws Exception {
        GameData game = new GameData(null, null, null, "the Game", null);
        game = gameDAO.addGame(game);
        game = game.setWhitePlayer("bob");
        ChessMove movePawn = new ChessMove(new ChessPosition(2,1), new ChessPosition(3, 1), null);
        ChessGame chessGame = game.game();
        chessGame.makeMove(movePawn);
        game = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), chessGame);
        gameDAO.updateGameData(game);
        Assertions.assertEquals(game.game(), gameDAO.getGameData(game.gameID()).game(),
                "chessGame didn't update as expected");
    }

}
