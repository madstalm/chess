package server.serverwebsocket;

import com.google.gson.Gson;

import dataaccess.*;
import service.*;
import model.*;
import chess.*;

import org.eclipse.jetty.websocket.api.annotations.*;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.*;

import websocket.commands.*;
import websocket.messages.*;
import websocket.messages.ServerMessage.ServerMessageType;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections;
    private final AuthService authService;
    private final GameService gameService;

    public WebSocketHandler(AuthService authService, GameService gameService) {
        this.authService = authService;
        this.gameService = gameService;
        connections = new ConnectionManager();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case CONNECT -> connect(command, session);
            case MAKE_MOVE -> makeMove(message, session);
            case LEAVE -> leave(command, session);
            case RESIGN -> resign(command, session);
        }
    }

    private void connect(UserGameCommand command, Session session) throws Exception {
        //I need to validate that the ConnectCommand class is kosher with the tests and stuff
        /*
        ConnectCommand command = new Gson().fromJson(message, ConnectCommand.class);
        try {
            AuthData authData = authService.checkAuth(command.getAuthToken());
            connections.add(authData.authToken(), command.getGameID(), session);
            String sendMessage = "";
            if (command.getTeamColor() == null) { //an observer has joined
                sendMessage = String.format("%s has joined the game as observer", authData.username());
            }
            else { //a player has joined
                sendMessage = String.format("%s has joined the game as %s", authData.username(), command.getTeamColorString());
            }
            var notification = new NotificationMessage(ServerMessageType.NOTIFICATION, sendMessage);
            connections.broadcastUser(authData.authToken(), findLoadGame(command.getGameID()));
            connections.broadcast(authData.authToken(), command.getGameID(), notification);
        }
        */
        try {
            AuthData authData = authService.checkAuth(command.getAuthToken());
            connections.add(authData.authToken(), command.getGameID(), session);
            String sendMessage = "";
            PlayerData player = getPlayer(authData.username(), command.getGameID());
            if (player.username()!=null) { //a player has joined
                sendMessage = String.format("%s has joined the game as %s", player.username(), player.playerColor().toString());
            }
            else { //an observer has joined
                sendMessage = String.format("%s has joined the game as observer", authData.username());
            }
            var notification = new NotificationMessage(ServerMessageType.NOTIFICATION, sendMessage);
            connections.broadcastUser(authData.authToken(), findLoadGame(command.getGameID()));
            connections.broadcast(authData.authToken(), command.getGameID(), notification);
        }
        catch (UnauthorizedException e) {
            var msg = new Gson().toJson(new ErrorMessage(ServerMessageType.ERROR, "Error: unauthorized connection"));
            session.getRemote().sendString(msg);
        }
    }

    private void makeMove(String message, Session session) throws Exception {
        MakeMoveCommand command = new Gson().fromJson(message, MakeMoveCommand.class);
        try {
            AuthData authData = authService.checkAuth(command.getAuthToken());
            PlayerData player = getPlayer(authData.username(), command.getGameID());
            if (player.opponent() == null) {
                throw new InvalidMoveException(String.format("there is no % player", player.opponentColor().toString()));
            }
            ChessGame game =  gameService.getGame(command.getGameID());
            //make the move
            game.makeMove(command.getMove());
            LoadGameMessage updatedGame = new LoadGameMessage(ServerMessageType.LOAD_GAME, game);
            //send out the updated game to clients
            connections.broadcastUser(authData.authToken(), updatedGame);
            connections.broadcast(authData.authToken(), command.getGameID(), updatedGame);
            //broadcast moves to opponents and observers
            String moveMessage = String.format("%s player % made move %s", player.playerColor().toString(),
                    player.username(), command.getMove().toString());
            NotificationMessage moveBroadcast = new NotificationMessage(ServerMessageType.NOTIFICATION, moveMessage);
            connections.broadcast(authData.authToken(), command.getGameID(), moveBroadcast);
            //handler updates to game state
            if (game.isInCheckmate(player.opponentColor())) {
                NotificationMessage checkmateMessage = new NotificationMessage(ServerMessageType.NOTIFICATION,
                        String.format("congratulations %s player %s, %s player %s is in check",
                        player.playerColor().toString(), player.username(), player.opponentColor().toString(), player.opponent()));
                connections.broadcastUser(authData.authToken(), checkmateMessage);
                connections.broadcast(authData.authToken(), command.getGameID(), checkmateMessage);
            }
            else if (game.isInCheck(player.opponentColor())) {
                NotificationMessage checkMessage = new NotificationMessage(ServerMessageType.NOTIFICATION,
                        String.format("%s player %s is in check", player.opponentColor().toString(), player.opponent()));
                connections.broadcastUser(authData.authToken(), checkMessage);
                connections.broadcast(authData.authToken(), command.getGameID(), checkMessage);
            }
            else if (game.isInStalemate(player.opponentColor())) {
                NotificationMessage staleMessage = new NotificationMessage(ServerMessageType.NOTIFICATION,
                        "the game has ended in a stalemate");
                connections.broadcastUser(authData.authToken(), staleMessage);
                connections.broadcast(authData.authToken(), command.getGameID(), staleMessage);
            }
            //update the game in the db
            gameService.updateGame(command.getGameID(), game);
        }
        catch (UnauthorizedException e) {
            var msg = new Gson().toJson(new ErrorMessage(ServerMessageType.ERROR, "Error: unauthorized"));
            session.getRemote().sendString(msg);
        }
        catch (InvalidMoveException e) {
            var errorMessage = new ErrorMessage(ServerMessageType.ERROR, "Error: " + e.getMessage());
            session.getRemote().sendString(new Gson().toJson(errorMessage));
        }
        catch (DataAccessException e) {
            var msg = new Gson().toJson(new ErrorMessage(ServerMessageType.ERROR, "Error: server unable to access game"));
            session.getRemote().sendString(msg);
        }
    }

    private void leave(UserGameCommand command, Session session) throws Exception {
        try {
            AuthData authData = authService.checkAuth(command.getAuthToken());
            //remove player from game if not observer
            PlayerData player = getPlayer(authData.username(), command.getGameID());
            if (player.username()!=null) {
                GameData game = gameService.getGameData(command.getGameID());
                GameData updatedGame = game;
                switch (player.playerColor()) {
                    case WHITE:
                        updatedGame = new GameData(game.gameID(), null, game.blackUsername(), game.gameName(), game.game());
                    case BLACK:
                        updatedGame = new GameData(game.gameID(), game.whiteUsername(), null, game.gameName(), game.game());
                }
                gameService.updateGameData(updatedGame);
            }
            //remove connection and notify clients
            connections.remove(authData.authToken());
            var message = String.format("%s left the game", authData.username());
            var notification = new NotificationMessage(ServerMessageType.NOTIFICATION, message);
            connections.broadcast(authData.authToken(), command.getGameID(), notification);
        }
        catch (UnauthorizedException e) {
            var msg = new Gson().toJson(new ErrorMessage(ServerMessageType.ERROR, "Error: unauthorized"));
            session.getRemote().sendString(msg);
        }
        catch (DataAccessException e) {
            var msg = new Gson().toJson(new ErrorMessage(ServerMessageType.ERROR, "Error: server unable to access game"));
            session.getRemote().sendString(msg);
        }
    }

    private void resign(UserGameCommand command, Session session) throws Exception {
        try {
            AuthData authData = authService.checkAuth(command.getAuthToken());
            PlayerData player = getPlayer(authData.username(), command.getGameID());
            ChessGame game =  gameService.getGame(command.getGameID());
            game.finishGame();
            //update the game in the db
            gameService.updateGame(command.getGameID(), game);
            //broadcast result
            var message = String.format("%s player %s has resigned from the game; %s player %s wins",
                    player.playerColor().toString(), player.username(), player.opponentColor().toString(), player.opponent());
            var notification = new NotificationMessage(ServerMessageType.NOTIFICATION, message);
            connections.broadcast(authData.authToken(), command.getGameID(), notification);
            connections.broadcastUser(authData.authToken(), notification);
        }
        catch (UnauthorizedException e) {
            var msg = new Gson().toJson(new ErrorMessage(ServerMessageType.ERROR, "Error: unauthorized"));
            session.getRemote().sendString(msg);
        }
        catch (DataAccessException e) {
            var msg = new Gson().toJson(new ErrorMessage(ServerMessageType.ERROR, "Error: server unable to access game"));
            session.getRemote().sendString(msg);
        }
    }

    private ServerMessage findLoadGame(Integer gameID) {
        try {
            ChessGame game =  gameService.getGame(gameID);
            return new LoadGameMessage(ServerMessageType.LOAD_GAME, game);
        }
        catch (Exception e) {
            return new ErrorMessage(ServerMessageType.ERROR, "Error: server unable to access game");
        }
    }

    private PlayerData getPlayer(String username, Integer gameID) throws DataAccessException {
        GameData game = gameService.getGameData(gameID);
        String playerUsername = null;
        String opponent = null;
        ChessGame.TeamColor playerColor = null;
        ChessGame.TeamColor opponentColor = null;
        if (username == game.whiteUsername()) {
            playerUsername = username;
            opponent = game.blackUsername();
            playerColor = ChessGame.TeamColor.WHITE;
            opponentColor = ChessGame.TeamColor.BLACK;
        }
        else if (username == game.blackUsername()) {
            playerUsername = username;
            opponent = game.whiteUsername();
            playerColor = ChessGame.TeamColor.BLACK;
            opponentColor = ChessGame.TeamColor.WHITE;
        }
        return new PlayerData(playerUsername, gameID, playerColor, opponent, opponentColor);
    }

}
