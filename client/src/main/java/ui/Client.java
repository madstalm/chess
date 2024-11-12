package ui;
import ui.*;
import model.*;

import java.util.Arrays;

import com.google.gson.Gson;

public class Client {
    private AuthData token = null;
    private final ServerFacade server;
    private final String serverUrl;
    private boolean loggedIn = false;

    public Client(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "logout" -> logout();
                case "creategame" -> createGame(params);
                case "listgames" -> listGames();
                case "playgame" -> playGame();
                case "observegame" -> observeGame();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ClientException e) {
            return e.getMessage();
        }
    }

    public String login(String... params) throws ClientException {
        if (loggedIn) {
            return "Already logged in";
        }
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];
            UserData user = new UserData(username, password, null);
            try {
                token = server.login(user);
                loggedIn = true;
                return String.format("Welcome %s", username);
            }
            catch (Exception e) {
                throw new ClientException("<username> or <password> incorrect");
            }
        }
        throw new ClientException("Expected: <username> <password>");
    }

    public String register(String... params) throws ClientException {
        if (loggedIn) {
            return "Already logged in";
        }
        if (params.length == 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            UserData user = new UserData(username, password, email);
            try {
                token = server.register(user);
                loggedIn = true;
                return String.format("Welcome %s", username);
            }
            catch (Exception e) {
                throw new ClientException("<username>: " + username + " already taken");
            }
        }
        throw new ClientException("Expected: <username> <password> <email>");
    }

    public String logout() throws ClientException {
        assertLoggedIn();
        try {
            server.logout(token);
            token = null;
            loggedIn = false;
            return "Thanks for playing";
        }
        catch (Exception e) {
            throw new ClientException(e.getMessage());
        }
    }

    public String createGame(String... params) throws ClientException {
        assertLoggedIn();
        if (params.length == 1) {
            GameData game = new GameData(null, null, null, params[0], null);
            try {
                int gameID = server.createGame(game, token);
                //not sure what to do next? Some kind of a mapping system that stays consistent and updates when new games are created or something I guess
            }
            
        }
        throw new ClientException("Expected: <game name>");
    }

    public String listGames() throws ClientException {
        assertLoggedIn();
        var games = server.listGames();
        var result = new StringBuilder();
        var gson = new Gson();
        for (var game : games) {
            result.append(gson.toJson(game)).append('\n');
        }
        return result.toString();
    }

    public String playGame(String... params) throws ClientException {
        assertLoggedIn();
    }

    public String observeGame(String... params) throws ClientException {
        assertLoggedIn();
    }
    

    public String help() {
        if (loggedIn == false) {
            return """
                    - login <username> <password>
                    - register <original username> <password> <email>
                    - help
                    - quit
                    """;
        }
        return """
                    - help
                    - logout
                    - createGame <game name>
                    - listGames
                    - playGame <game name> <team color>
                    - observeGame <game name>
                """;
    }

    private void assertLoggedIn() throws ClientException {
        if (loggedIn == false) {
            throw new ClientException("You must sign in");
        }
    }
}
