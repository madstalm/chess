package ui;
import model.*;
import chess.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class Client {
    private AuthData token = null;
    private final ServerFacade server;
    private final String serverUrl;
    private boolean loggedIn = false;
    private boolean playingGame = false;
    private boolean observingGame = false;
    /**
     * stores localID as key, GameData as value.
     * GameData does not store a ChessGame instance.
    */
    private Map<Integer, GameData> gamesMap = new HashMap<>();
    /**
     * stores localID as key, gameName as value
    */
    private Map<Integer, String> gamesNames = new HashMap<>();
    /**
     * stores gameID as key, localID as value
    */
    private Map<Integer, Integer> gamesIDs = new HashMap<>();
    private int availableGames;

    public Client(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.availableGames = 0;
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
                case "playgame" -> playGame(params);
                case "observegame" -> observeGame(params);
                case "redrawboard" -> redrawBoard();
                case "leave" -> leave();
                case "makemove" -> makeMove(params);
                case "resign" -> resign();
                case "legalmoves" -> legalMoves(params);
                case "quit" -> quit();
                default -> help();
            };
        } catch (ClientException e) {
            return e.getMessage();
        }
    }

    public String login(String... params) throws ClientException {
        if (loggedIn) {
            return "Already logged in\n";
        }
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];
            UserData user = new UserData(username, password, null);
            try {
                token = server.login(user);
                loggedIn = true;
                return String.format("Welcome %s\n", username);
            }
            catch (Exception e) {
                throw new ClientException("<username> or <password> incorrect");
            }
        }
        throw new ClientException("Expected: <username> <password>");
    }

    public String register(String... params) throws ClientException {
        if (loggedIn) {
            return "Already logged in\n";
        }
        if (params.length == 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            UserData user = new UserData(username, password, email);
            try {
                token = server.register(user);
                loggedIn = true;
                return String.format("Welcome %s\n", username);
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
            return "Thanks for playing \n";
        }
        catch (Exception e) {
            throw new ClientException(e.getMessage());
        }
    }

    public String createGame(String... params) throws ClientException {
        assertLoggedIn();
        if (params.length == 1) {
            GameData game = new GameData(null, null, null, params[0], null);
            if (!gamesNames.containsValue(game.gameName())) {
                try {
                    int gameID = server.createGame(game, token);
                    GameData newGame = new GameData(gameID, game.whiteUsername(), game.blackUsername(),
                    game.gameName(), null);
                    ++this.availableGames;
                    gamesMap.put(this.availableGames, newGame);
                    gamesNames.put(this.availableGames, newGame.gameName());
                    gamesIDs.put(gameID, this.availableGames);
                    return params[0] + " added to available games\n";
                }
                catch (Exception e) {
                    throw new ClientException("Failed to create game");
                }
            }
            throw new ClientException(String.format("Game named %s already exists", game.gameName()));
        }
        throw new ClientException("Expected: <game name>");
    }

    public String listGames() throws ClientException {
        assertLoggedIn();
        try {
            GameData[] games = server.listGames(token);
            var result = new StringBuilder();
            result.append("Available games:\n");
            for (GameData game : games) { //add games from server to local map
                GameData addGame = new GameData(game.gameID(), game.whiteUsername(), game.blackUsername(),
                        game.gameName(), null);
                if (!gamesNames.containsValue(addGame.gameName())) {
                    ++this.availableGames;
                    gamesMap.put(this.availableGames, addGame);
                    gamesNames.put(this.availableGames, addGame.gameName());
                    gamesIDs.put(addGame.gameID(), this.availableGames);
                }
                else {//update the game if already stored locally
                    Integer localID = gamesIDs.get(addGame.gameID());
                    gamesMap.put(localID, addGame);
                }
            }
            return gameLister(result).toString();
        }
        catch (Exception e) {
            throw new ClientException("Failed to retrieve games");
        }
    }

    public String playGame(String... params) throws ClientException {
        assertLoggedIn();
        if (params.length == 2) {
            String paramsRecombined = params[0] + " " + params[1];
            if (paramsRecombined.matches("\\d+\\s+(?i)(white|black)") &&
                        (gamesMap.get(Integer.parseInt(params[0])) != null)) {
                Integer gameNumber = Integer.parseInt(params[0]);
                GameData game = gamesMap.get(gameNumber);
                try {
                    JoinGameRequest request = new JoinGameRequest(null, game.gameID());
                    DrawBoard artist = new DrawBoard(new ChessGame());
                    playingGame = true;
                    if (params[1].matches("(?i)\\s*white\\s*")) {
                        request = request.setPlayerColor(ChessGame.TeamColor.WHITE);
                        server.joinGame(request, token);
                        return artist.display(ChessGame.TeamColor.WHITE) + "\n\n" + help();
                    }
                    else  if (params[1].matches("(?i)\\s*black\\s*")){
                        request = request.setPlayerColor(ChessGame.TeamColor.BLACK);
                        server.joinGame(request, token);
                        return artist.display(ChessGame.TeamColor.BLACK) + "\n\n" + help();
                    }
                    else {
                        throw new ClientException("Unable to recognize team color");
                    }
                }
                catch (Exception e) {
                    throw new ClientException(String.format("Failed to join game %s", gameNumber));
                }
            }
            else {
                throw new ClientException("<game number> should be an integer\nTeam color should be either WHITE or BLACK");
            }
        }
        throw new ClientException("Expected: <game number> <WHITE|BLACK>");
    }

    public String observeGame(String... params) throws ClientException {
        assertLoggedIn();
        if (params.length == 1) {
            if (params[0].matches("\\d+")) {
                Integer gameNumber = Integer.parseInt(params[0]);
                GameData game = gamesMap.get(gameNumber);
                if (game != null) {
                    observingGame = true;
                    
                    DrawBoard artist = new DrawBoard(new ChessGame());
                    return artist.display(ChessGame.TeamColor.WHITE) + "\n\n" + help();
                }
                throw new ClientException(String.format("Game %s does not exist", gameNumber));
            }
            throw new ClientException("<game number> should be an integer");
        }
        throw new ClientException("Expected: <game number>");
    }

    public String quit() throws ClientException {
        try {
            if (loggedIn == true) {
                logout();
                playingGame = false;
                observingGame = false;
            }
            return "quit";
        }
        catch (Exception e) {
            throw new ClientException(e.getMessage());
        }
    }

    //redrawBoard - legalMoves could be called in some kind of WebsocketHandler class, which will do most of the implementation
    //they exist in client so that they can have access to variables like loggedIn, playingGame, etc.

    //could be called as the final step in playGame() and observeGame()
    public String redrawBoard() throws ClientException {
        assertLoggedIn();
        if ((playingGame == false)&&(observingGame == false)) {
            return help();
        }
        return "Error: not implemented";
    }

    public String leave() throws ClientException {
        assertLoggedIn();
        if ((playingGame == false)&&(observingGame == false)) {
            return help();
        }

        playingGame = false;
        observingGame = false;
        return "Error: not implemented";
    }

    public String makeMove(String... params) throws ClientException {
        assertLoggedIn();
        if (playingGame == false) {
            return help();
        }
        return "Error: not implemented";
    }

    public String resign() throws ClientException {
        assertLoggedIn();
        if (playingGame == false) {
            return help();
        }

        return "Error: not implemented";
    }

    //could be nearly the same as redrawBoard(), but with some kind of array passed in
    public String legalMoves(String... params) throws ClientException {
        assertLoggedIn();
        if ((playingGame == false)&&(observingGame == false)) {
            return help();
        }
        return "Error: not implemented";
    }

    public String help() {
        if (loggedIn == false) {
            return """
                    - login <username> <password>
                    - register <unique username> <password> <email>
                    - help
                    - quit
                    """;
        }
        else if ((loggedIn == true)&&(playingGame == true)) {
            return """
                - help
                - redrawBoard
                - Leave
                - makeMove <origin:(a-h)(1-8)><destination:(a-h)(1-8)> [ex. "makeMove d1a4"]
                - resign
                - legalMoves <(a-h)(1-8)>
            """;
        }
        else if ((loggedIn == true)&&(observingGame == true)) {
            return """
                - help
                - redrawBoard
                - Leave
                - legalMoves <(a-h)(1-8)> [ex. "legalMoves d1"]
            """;
        }
        return """
                    - help
                    - logout
                    - createGame <game name>
                    - listGames
                    - playGame <game number> <[WHITE|BLACK]>
                    - observeGame <game number>
                """;
    }

    private StringBuilder gameLister(StringBuilder result) {
        for (Map.Entry<Integer, GameData> entry : gamesMap.entrySet()) { //iterate through map to display games
            result.append(entry.getKey()).append(". ").append(entry.getValue().gameName());
            String whitePlayer = entry.getValue().whiteUsername();
            String blackPlayer = entry.getValue().blackUsername();
            if (whitePlayer != null) {
                result.append(" [WHITE: ").append(whitePlayer).append(']');
            }
            if (blackPlayer != null) {
                result.append(" [BLACK: ").append(blackPlayer).append(']');
            }
            if ((whitePlayer == null) && (blackPlayer == null)) {
                result.append(" [no players]");
            }
            result.append("\n");
        }
        return result;
    }

    private void assertLoggedIn() throws ClientException {
        if (loggedIn == false) {
            throw new ClientException("You must sign in");
        }
    }
}
