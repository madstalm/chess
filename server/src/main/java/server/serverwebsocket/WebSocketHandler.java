package server.serverwebsocket;

import com.google.gson.Gson;

import dataaccess.UnauthorizedException;
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
        }
        catch (UnauthorizedException e) {
            var msg = new Gson().toJson(new ErrorMessage(ServerMessageType.ERROR, "Error: unauthorized"));
            session.getRemote().sendString(msg);
        }
    }

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

}
