package server.serverwebsocket;

import com.google.gson.Gson;

import dataaccess.*;
import service.*;
import model.*;
import chess.*;

import org.eclipse.jetty.websocket.api.annotations.*;

import java.io.IOException;

import javax.management.Notification;

import org.eclipse.jetty.websocket.api.*;

import websocket.commands.*;
import websocket.messages.*;
import websocket.messages.ServerMessage.ServerMessageType;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections;
    private final AuthService authService;
    private final GameService gameService;
    private final UserService userService;

    public WebSocketHandler(AuthService authService, GameService gameService, UserService userService) {
        this.authService = authService;
        this.gameService = gameService;
        this.userService = userService;
        connections = new ConnectionManager();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        //check authorization for the command with the authtoken
        switch (command.getCommandType()) {
            case CONNECT -> connect(message, session);
            case MAKE_MOVE -> makeMove(message, session);
            case LEAVE -> leave(command, session);
            case RESIGN -> resign(command, session);
        }
    }

    private void connect(String message, Session session) throws Exception {
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
            game.makeMove(command.getMove());
            LoadGameMessage updatedGame = new LoadGameMessage(ServerMessageType.LOAD_GAME, game);
            connections.broadcastUser(authData.authToken(), updatedGame);
            connections.broadcast(authData.authToken(), command.getGameID(), updatedGame);
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
            //updates the game in the db
            gameService.updateGameData(command.getGameID(), game);
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

    //need to check if when I leave a game if I am just leaving the game or leaving the session altogether
    private void leave(UserGameCommand command, Session session) throws Exception {
        try {
            AuthData authData = authService.checkAuth(command.getAuthToken());
            connections.remove(authData.authToken());
            var message = String.format("%s left the game", authData.username());
            var notification = new NotificationMessage(ServerMessageType.NOTIFICATION, message);
            connections.broadcast(authData.authToken(), command.getGameID(), notification);
        }
        catch (UnauthorizedException e) {
            var msg = new Gson().toJson(new ErrorMessage(ServerMessageType.ERROR, "Error: unauthorized"));
            session.getRemote().sendString(msg);
        }
    }

    private void resign(UserGameCommand command, Session session) throws Exception {
        try {
            AuthData authData = authService.checkAuth(command.getAuthToken());
        }
        catch (UnauthorizedException e) {
            var msg = new Gson().toJson(new ErrorMessage(ServerMessageType.ERROR, "Error: unauthorized"));
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
        String opponent = game.blackUsername();
        ChessGame.TeamColor playerColor = ChessGame.TeamColor.WHITE;
        ChessGame.TeamColor opponentColor = ChessGame.TeamColor.BLACK;
        if (username == game.blackUsername()) {
            opponent = game.whiteUsername();
            playerColor = ChessGame.TeamColor.BLACK;
            opponentColor = ChessGame.TeamColor.WHITE;
        }
        return new PlayerData(username, gameID, playerColor, opponent, opponentColor);
    }

}
