package ui;
import model.*;
import chess.*;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import static ui.EscapeSequences.*;

import com.google.gson.Gson;

public class Client {
    private AuthData token = null;
    private final ServerFacade server;
    private final String serverUrl;
    private final ServerMessageHandler serverMessageHandler;
    private WebSocketFacade ws;
    private boolean loggedIn = false;
    private boolean playingGame = false;
    private boolean observingGame = false;
    private ChessGame currentGame;
    private Integer currentGameID;
    private ChessGame.TeamColor currentTeam;
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

    public Client(String serverUrl, ServerMessageHandler serverMessageHandler) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this.availableGames = 0;
        this.serverMessageHandler = serverMessageHandler;
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
                case "resign" -> resign(params);
                case "legalmoves" -> legalMoves(params);
                case "quit" -> quit();
                default -> help();
            };
        } catch (ClientException e) {
            return e.getMessage();
        }
    }

    public String login(String... params) throws ClientException {
        if ((playingGame)||(observingGame)) {
            return help();
        }
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
        if ((playingGame)||(observingGame)) {
            return help();
        }
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
        if ((playingGame)||(observingGame)) {
            return help();
        }
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
        if ((playingGame)||(observingGame)) {
            return help();
        }
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
        if ((playingGame)||(observingGame)) {
            return help();
        }
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
        if ((playingGame)||(observingGame)) {
            return help();
        }
        if (params.length == 2) {
            String paramsRecombined = params[0] + " " + params[1];
            if (paramsRecombined.matches("\\d+\\s+(?i)(white|black)") &&
                        (gamesMap.get(Integer.parseInt(params[0])) != null)) {
                Integer gameNumber = Integer.parseInt(params[0]);
                GameData game = gamesMap.get(gameNumber);
                try {
                    JoinGameRequest request = new JoinGameRequest(null, game.gameID());
                    playingGame = true;
                    currentGameID = game.gameID();
                    if (params[1].matches("(?i)\\s*white\\s*")) {
                        request = request.setPlayerColor(ChessGame.TeamColor.WHITE);
                        currentTeam = ChessGame.TeamColor.WHITE;
                        server.joinGame(request, token);
                        ws = new WebSocketFacade(serverUrl, serverMessageHandler);
                        ws.connect(token.authToken(), game.gameID());
                        return "\n";
                    }
                    else  if (params[1].matches("(?i)\\s*black\\s*")){
                        request = request.setPlayerColor(ChessGame.TeamColor.BLACK);
                        currentTeam = ChessGame.TeamColor.BLACK;
                        server.joinGame(request, token);
                        ws = new WebSocketFacade(serverUrl, serverMessageHandler);
                        ws.connect(token.authToken(), game.gameID());
                        return "\n";
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
        if ((playingGame)||(observingGame)) {
            return help();
        }
        if (params.length == 1) {
            if (params[0].matches("\\d+")) {
                Integer gameNumber = Integer.parseInt(params[0]);
                GameData game = gamesMap.get(gameNumber);
                if (game != null) {
                    observingGame = true;
                    currentGameID = gameNumber;
                    ws = new WebSocketFacade(serverUrl, serverMessageHandler);
                    ws.connect(token.authToken(), game.gameID());
                    return "\n";
                }
                throw new ClientException(String.format("Game %s does not exist", gameNumber));
            }
            throw new ClientException("<game number> should be an integer");
        }
        throw new ClientException("Expected: <game number>");
    }

    public String quit() throws ClientException {
        if ((playingGame)||(observingGame)) {
            return "you must leave the game first\n" + help();
        }
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
    public String redrawBoard() throws ClientException {
        assertLoggedIn();
        if ((playingGame == false)&&(observingGame == false)) {
            return help();
        }
        DrawBoard artist = new DrawBoard(currentGame, new ArrayList<>());
        return artist.display(getPlayerColor());
    }

    public String leave() throws ClientException {
        assertLoggedIn();
        if ((playingGame == false)&&(observingGame == false)) {
            return help();
        }
        ws.leave(token.authToken(), currentGameID);
        Integer localGameID = gamesIDs.get(currentGameID);
        currentGame = null;
        currentGameID = null;
        currentTeam = null;
        ws = null;
        playingGame = false;
        observingGame = false;
        return String.format("left game %d", localGameID);
    }

    public String makeMove(String... params) throws ClientException {
        assertLoggedIn();
        if (playingGame == false) {
            return help();
        }
        ChessMove move;
        if (params.length == 1) {
            move = validateMoveString(params[0], null);
            ws.makeMove(token.authToken(), currentGameID, move);
            return "\n";
        }
        else if (params.length == 2) {
            move = validateMoveString(params[0], params[1]);
            ws.makeMove(token.authToken(), currentGameID, move);
            return "\n";
        }
        else {
            throw new ClientException("Error: incorrect input");
        }
    }

    public String resign(String... params) throws ClientException {
        assertLoggedIn();
        if (!playingGame) {
            return help();
        }
        if (params.length == 1) {
            if (params[0].equals("y")) {
                ws.resign(token.authToken(), currentGameID);
                return "\n";
            }
            else {
                return "resignation declined\n";
            }
        }
        throw new ClientException("Error: no confirmation provided");
    }

    //could be nearly the same as redrawBoard(), but with some kind of array passed in
    public String legalMoves(String... params) throws ClientException {
        assertLoggedIn();
        if ((playingGame == false)&&(observingGame == false)) {
            return help();
        }
        ChessPosition position;
        if (params.length == 1) {
            position = validatePositionString(params[0]);
            Collection<ChessMove> moves = currentGame.validMoves(position);
            ArrayList<ChessPosition> positions = new ArrayList<>();
            for (ChessMove move : moves) {
                positions.add(move.getEndPosition());
            }
            positions.add(position);
            DrawBoard artist = new DrawBoard(currentGame, positions);
            return artist.display(getPlayerColor());
        }
        else {
            throw new ClientException("Error: incorrect input");
        }
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
                    - makeMove <origin:(a-h)(1-8)><destination:(a-h)(1-8)> <promotion piece if applicable> [ex. "makeMove d1a4"]
                    - resign <y|n>
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

    public ChessGame.TeamColor getPlayerColor() {
        if (currentTeam == ChessGame.TeamColor.BLACK) {
            return currentTeam;
        }
        else {
            return ChessGame.TeamColor.WHITE;
        }
    }

    public void setGame(ChessGame game) {
        currentGame = game;
    }

    private ChessMove validateMoveString(String inputMove, String inputPromotion) throws ClientException {
        if (inputMove.matches("^[a-h][1-8][a-h][1-8]$")) {
            ChessPosition start = new ChessPosition(Integer.parseInt(inputMove.substring(1, 2)),
                    alphaToCol(inputMove.substring(0, 1)));
            ChessPosition end = new ChessPosition(Integer.parseInt(inputMove.substring(inputMove.length() - 1)),
                    alphaToCol(inputMove.substring(2, 3)));
            ChessPiece.PieceType promotionPiece = null;
            if (inputPromotion != null) {
                promotionPiece = strToPiece(inputPromotion);
            }
            return new ChessMove(start, end, promotionPiece);
        }
        else {
            throw new ClientException("Error: incorrect input");
        }
    }

    private ChessPosition validatePositionString(String inputPosition) throws ClientException {
        if (inputPosition.matches("^[a-h][1-8]$")) {
            ChessPosition start = new ChessPosition(Integer.parseInt(inputPosition.substring(inputPosition.length() - 1)),
                    alphaToCol(inputPosition.substring(0, 1)));
            return start;
        }
        else {
            throw new ClientException("Error: incorrect input");
        }
    }

    private Integer alphaToCol(String alpha) throws ClientException {
        return switch(alpha) {
            case "a" -> 1;
            case "b" -> 2;
            case "c" -> 3;
            case "d" -> 4;
            case "e" -> 5;
            case "f" -> 6;
            case "g" -> 7;
            case "h" -> 8;
            default -> throw new ClientException("Error: attempted to move off the board");
        };
    }

    private ChessPiece.PieceType strToPiece(String input) throws ClientException {
        return switch (input) {
            case "queen" -> ChessPiece.PieceType.QUEEN;
            case "bishop" -> ChessPiece.PieceType.BISHOP;
            case "knight" -> ChessPiece.PieceType.KNIGHT;
            case "rook" -> ChessPiece.PieceType.ROOK;
            default -> throw new ClientException("Error: " + input + " is not a valid chess piece promotion");
        };
    }

}
