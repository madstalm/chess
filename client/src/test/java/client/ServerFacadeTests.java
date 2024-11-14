package client;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.*;
import java.net.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import server.Server;
import ui.ServerFacade;
import model.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;
    static String connectionUrl;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        connectionUrl = "http://localhost:" + port;
        facade = new ServerFacade(connectionUrl);
    }

    @AfterEach
    public void clearDatabase() throws IOException {//Send DELETE request to the /db endpoint to clear the database
        try {
            URL url = (new URI(connectionUrl + "/db")).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("DELETE");
            http.setReadTimeout(5000);
            int responseCode = http.getResponseCode();
            if ((responseCode != HttpURLConnection.HTTP_OK) && (responseCode != HttpURLConnection.HTTP_NO_CONTENT)) {
                throw new IOException("Database failed to clear: " + responseCode);
            }
            
            http.disconnect();
        }
        catch (Exception e) {
            throw new IOException("Database failed to clear: unable to connect");
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    @DisplayName("register")
    public void registerTest() throws Exception {
        UserData newUser = new UserData("a", "b", "c@d.com");
        AuthData auth = facade.register(newUser);
        Assertions.assertEquals(newUser.username(), auth.username(),
                "facade did not return auth for user a");
    }

    @Test
    @DisplayName("register negative")
    public void registerTestNegative() throws Exception {
        UserData newUser = new UserData("a", "b", "c@d.com");
        facade.register(newUser);
        UserData newUser2 = new UserData("a", "e", "f@g.com");
        Assertions.assertThrows(Exception.class,
                () -> facade.register(newUser2));
    }

    @Test
    @DisplayName("login")
    public void loginTest() throws Exception {
        UserData newUser = new UserData("a", "b", "c@d.com");
        AuthData auth = facade.register(newUser);
        facade.logout(auth);
        auth = facade.login(newUser);
        Assertions.assertEquals(newUser.username(), auth.username(),
                "login did not return appropriate AuthData");
    }

    @Test
    @DisplayName("login negative")
    public void loginTestNegative() throws Exception {
        UserData newUser = new UserData("a", "b", "c@d.com");
        facade.register(newUser);
        UserData fakeUser = new UserData("a", "e", "c@d.com");
        Assertions.assertThrows(Exception.class,
                () -> facade.login(fakeUser));
    }

    @Test
    @DisplayName("logout")
    public void logoutTest() throws Exception {
        UserData newUser = new UserData("a", "b", "c@d.com");
        AuthData auth = facade.register(newUser);
        Assertions.assertDoesNotThrow(() -> facade.logout(auth));
    }

    @Test
    @DisplayName("logout negative")
    public void logoutTestNegative() throws Exception {
        UserData newUser = new UserData("a", "b", "c@d.com");
        facade.register(newUser);
        AuthData badAuth = new AuthData("clearly wrong", newUser.username());
        Assertions.assertThrows(Exception.class,
                () -> facade.logout(badAuth));
    }

    @Test
    @DisplayName("listGames")
    public void listGamesTest() throws Exception {
        UserData newUser = new UserData("a", "b", "c@d.com");
        AuthData auth = facade.register(newUser);
        GameData newGame = new GameData(null, null, null, "newGame", null);
        facade.createGame(newGame, auth);
        Assertions.assertEquals(newGame.gameName(), facade.listGames(auth)[0].gameName(),
                "Did not return the only game created");
    }

    @Test
    @DisplayName("listGames negative")
    public void listGamesTestNegative() throws Exception {
        UserData newUser = new UserData("a", "b", "c@d.com");
        facade.register(newUser);
        AuthData badAuth = new AuthData("clearly wrong", newUser.username());
        Assertions.assertThrows(Exception.class,
                () -> facade.listGames(badAuth));
    }

    @Test
    @DisplayName("createGame")
    public void createGameTest() throws Exception {
        UserData newUser = new UserData("a", "b", "c@d.com");
        AuthData auth = facade.register(newUser);
        GameData newGame = new GameData(null, null, null, "newGame", null);
        Object result = facade.createGame(newGame, auth);
        assertTrue(result instanceof Integer, "Expected result to be a gameID (Integer)");
    }

    @Test
    @DisplayName("createGame negative")
    public void createGameTestNegative() throws Exception {
        UserData newUser = new UserData("a", "b", "c@d.com");
        AuthData auth = facade.register(newUser);
        GameData newGame = new GameData(null, null, null, "", null);
        Assertions.assertThrows(Exception.class, 
                () -> facade.createGame(newGame, auth));
    }

    @Test
    @DisplayName("joinGame")
    public void joinGameTest() throws Exception {
        Assertions.assertTrue(true);
    }

    @Test
    @DisplayName("joinGame negative")
    public void joingGameTestNegative() throws Exception {
        Assertions.assertTrue(true);
    }

}
