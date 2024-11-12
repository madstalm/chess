package ui;
import ui.*;

import java.util.Arrays;

import com.google.gson.Gson;

public class Client {
    private String visitorName = null;
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
                case "listgames" -> listGames(params);
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
        if (params.length >= 1) {
            loggedIn = true;
            visitorName = String.join("-", params);
            return String.format("You signed in as %s.", visitorName);
        }
        throw new ClientException("Expected: <username> <password>");
    }

    public String logout() throws ClientException {
        assertLoggedIn();
        loggedIn = false;
        return String.format("Thanks for playing, %s", visitorName);
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
