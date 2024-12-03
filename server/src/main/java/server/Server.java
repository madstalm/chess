package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import chess.ChessGame;
import spark.*;
import java.util.*;

import dataaccess.AlreadyTakenException;
import dataaccess.DataAccessException;
import dataaccess.InvalidInputException;
import dataaccess.UnauthorizedException;
import model.*;
import websocket.commands.*;
import websocket.messages.*;
import server.serverwebsocket.*;
import service.AuthService;
import service.GameService;
import service.UserService;

public class Server {
    private final AuthService authService;
    private final GameService gameService;
    private final UserService userService;
    private final WebSocketHandler webSocketHandler;
    
    public Server(boolean startWithSQL) {
        dataaccess.AuthDAO authDAO = new dataaccess.MemoryAuthDAO();
        dataaccess.GameDAO gameDAO = new dataaccess.MemoryGameDAO();
        dataaccess.UserDAO userDAO = new dataaccess.MemoryUserDAO();
        if (startWithSQL) {
            try {
                authDAO = new dataaccess.SQLAuthDAO();
                gameDAO = new dataaccess.SQLGameDAO();
                userDAO = new dataaccess.SQLUserDAO();
            }
            catch (Exception ex) {
                System.err.println("Error initializing SQL DAOs, defaulting to memory DAOs: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        this.authService = new AuthService(authDAO);
        this.gameService = new GameService(gameDAO);
        this.userService = new UserService(userDAO);
        webSocketHandler = new WebSocketHandler();
    }
    
    public Server() {
        dataaccess.AuthDAO authDAO = new dataaccess.MemoryAuthDAO();
        dataaccess.GameDAO gameDAO = new dataaccess.MemoryGameDAO();
        dataaccess.UserDAO userDAO = new dataaccess.MemoryUserDAO();
        try {
            authDAO = new dataaccess.SQLAuthDAO();
            gameDAO = new dataaccess.SQLGameDAO();
            userDAO = new dataaccess.SQLUserDAO();
        }
        catch (Exception ex) {
            System.err.println("Error initializing SQL DAOs, defaulting to memory DAOs: " + ex.getMessage());
            ex.printStackTrace();
        }
        this.authService = new AuthService(authDAO);
        this.gameService = new GameService(gameDAO);
        this.userService = new UserService(userDAO);
        webSocketHandler = new WebSocketHandler();
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/ws", webSocketHandler);
        
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        
        Spark.exception(AlreadyTakenException.class, this::alreadyTakenException);
        Spark.exception(DataAccessException.class, this::dataAccessException);
        Spark.exception(InvalidInputException.class, this::invalidInputException);
        Spark.exception(UnauthorizedException.class, this::unauthorizedException);
        Spark.exception(JsonSyntaxException.class, this::jsonSyntaxException);
        Spark.exception(Exception.class, this::exception);
        
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
    
    private Object clear(Request req, Response res) throws Exception {
        authService.clear();
        gameService.clear();
        userService.clear();
        res.status(200);
        return "";
    }

    private Object register(Request req, Response res) throws Exception {
        res.type("application/json");
        var user = new Gson().fromJson(req.body(), UserData.class);
        userService.registerUser(user);
        AuthData authorization = authService.createAuth(user);
        res.status(200);
        return new Gson().toJson(authorization);
    }

    private Object login(Request req, Response res) throws Exception {
        res.type("application/json");
        var user = new Gson().fromJson(req.body(), UserData.class);
        UserData checked = userService.checkLogin(user);
        AuthData authorization = authService.createAuth(checked);
        res.status(200);
        return new Gson().toJson(authorization);
    }

    private Object logout(Request req, Response res) throws Exception {
        res.type("application/json");
        var token = req.headers("authorization");
        authService.checkLogout(token);
        res.status(200);
        return "";
    }

    private Object listGames(Request req, Response res) throws Exception {
        res.type("application/json");
        var token = req.headers("authorization");
        authService.checkAuth(token);
        Collection<ListGamesResponse> games = gameService.getGames();
        res.status(200);
        return new Gson().toJson(Map.of("games", games));
    }

    private Object createGame(Request req, Response res) throws Exception {
        res.type("application/json");
        var token = req.headers("authorization");
        authService.checkAuth(token);
        var game = new Gson().fromJson(req.body(), GameData.class);
        Integer gameID = gameService.gameCreator(game);
        res.status(200);
        return new Gson().toJson(Map.of("gameID", gameID));
    }

    private Object joinGame(Request req, Response res) throws Exception {
        res.type("application/json");
        var token = req.headers("authorization");
        AuthData authorization = authService.checkAuth(token);
        var request = new Gson().fromJson(req.body(), JoinGameRequest.class);
        Integer gameID = request.gameID();
        ChessGame.TeamColor playerColor = request.playerColor();
        gameService.gameJoiner(authorization, gameID, playerColor);
        res.status(200);
        return "";
    }

    // Error/Exception Handling
    private void unauthorizedException(UnauthorizedException ex, Request req, Response res) {
        res.status(401);
        res.body(new Gson().toJson(Map.of("message", ex.getMessage())));
    }

    private void jsonSyntaxException(JsonSyntaxException ex, Request req, Response res) {
        res.status(500);
        res.body(new Gson().toJson(Map.of("message", "Error: bad request")));
    }

    private void invalidInputException(InvalidInputException ex, Request req, Response res) {
        res.status(400);
        res.body(new Gson().toJson(Map.of("message", ex.getMessage())));
    }

    private void alreadyTakenException(AlreadyTakenException ex, Request req, Response res) {
        res.status(403);
        res.body(new Gson().toJson(Map.of("message", ex.getMessage())));
    }

    private void exception(Exception ex, Request req, Response res) {
        res.status(500);
        res.body(new Gson().toJson(Map.of("message", ex.getMessage())));
    }

    private void dataAccessException(DataAccessException ex, Request req, Response res) {
        res.status(500);
        res.body(new Gson().toJson(Map.of("message", ex.getMessage())));
    }

}
