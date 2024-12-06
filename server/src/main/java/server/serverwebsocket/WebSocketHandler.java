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
        try {
            AuthData authData = authService.checkAuth(command.getAuthToken());
            gameService.getGame(command.getGameID());
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
            var message = new Gson().toJson(new ErrorMessage(ServerMessageType.ERROR, "Error: unauthorized connection"));
            session.getRemote().sendString(message);
        }
        catch (DataAccessException e) {
            String msgString =  String.format("Error: game %d does not exist", command.getGameID());
            var message = new Gson().toJson(new ErrorMessage(ServerMessageType.ERROR, msgString));
            session.getRemote().sendString(message);
        }
    }

    private void makeMove(String message, Session session) throws Exception {
        MakeMoveCommand command = new Gson().fromJson(message, MakeMoveCommand.class);
        try {
            AuthData authData = authService.checkAuth(command.getAuthToken());
            gameService.getGame(command.getGameID());
            PlayerData player = getPlayer(authData.username(), command.getGameID());
            if (player.opponent() == null) {
                throw new InvalidMoveException("missing players or observing");
            }
            ChessGame game =  gameService.getGame(command.getGameID());
            //make the move
            checkPiece(command.getMove().getStartPosition(), player.playerColor(), game);
            game.makeMove(command.getMove());
            LoadGameMessage updatedGame = new LoadGameMessage(ServerMessageType.LOAD_GAME, game);
            //send out the updated game to clients
            connections.broadcastUser(authData.authToken(), updatedGame);
            connections.broadcast(authData.authToken(), command.getGameID(), updatedGame);
            //broadcast moves to opponents and observers
            String moveMessage = String.format("%s player %s made move %s", player.playerColor().toString(),
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
            var errorMessage = new Gson().toJson(new ErrorMessage(ServerMessageType.ERROR, "Error: unauthorized"));
            session.getRemote().sendString(errorMessage);
        }
        catch (InvalidMoveException e) {
            var errorMessage = new ErrorMessage(ServerMessageType.ERROR, "Error: " + e.getMessage());
            session.getRemote().sendString(new Gson().toJson(errorMessage));
        }
        catch (DataAccessException e) {
            var errorMessage = new Gson().toJson(new ErrorMessage(ServerMessageType.ERROR, "Error: server unable to access game"));
            session.getRemote().sendString(errorMessage);
        }
    }

    private void leave(UserGameCommand command, Session session) throws Exception {
        try {
            AuthData authData = authService.checkAuth(command.getAuthToken());
            gameService.getGame(command.getGameID());
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
            var message = new Gson().toJson(new ErrorMessage(ServerMessageType.ERROR, "Error: unauthorized"));
            session.getRemote().sendString(message);
        }
        catch (DataAccessException e) {
            var message = new Gson().toJson(new ErrorMessage(ServerMessageType.ERROR, "Error: server unable to access game"));
            session.getRemote().sendString(message);
        }
    }

    private void resign(UserGameCommand command, Session session) throws Exception {
        try {
            AuthData authData = authService.checkAuth(command.getAuthToken());
            gameService.getGame(command.getGameID());
            PlayerData player = getPlayer(authData.username(), command.getGameID());
            if (player.username() == null) {
                throw new InvalidMoveException("non-player attempt");
            }
            ChessGame game =  gameService.getGame(command.getGameID());
            if (game.gameFinished()) {
                throw new UnauthorizedException("game is already over");
            }
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
            var message = new Gson().toJson(new ErrorMessage(ServerMessageType.ERROR, "Error: unauthorized"));
            session.getRemote().sendString(message);
        }
        catch (DataAccessException e) {
            var message = new Gson().toJson(new ErrorMessage(ServerMessageType.ERROR, "Error: server unable to access game"));
            session.getRemote().sendString(message);
        }
        catch (InvalidMoveException e) {
            var message = new Gson().toJson(new ErrorMessage(ServerMessageType.ERROR, "Error: resignation not permited by non-players"));
            session.getRemote().sendString(message);
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
        if (username.equals(game.whiteUsername())) {
            playerUsername = username;
            opponent = game.blackUsername();
            playerColor = ChessGame.TeamColor.WHITE;
            opponentColor = ChessGame.TeamColor.BLACK;
        }
        else if (username.equals(game.blackUsername())) {
            playerUsername = username;
            opponent = game.whiteUsername();
            playerColor = ChessGame.TeamColor.BLACK;
            opponentColor = ChessGame.TeamColor.WHITE;
        }
        return new PlayerData(playerUsername, gameID, playerColor, opponent, opponentColor);
    }

    private void checkPiece(ChessPosition start, ChessGame.TeamColor playerColor, ChessGame game) throws UnauthorizedException {
        ChessPiece movingPiece = game.getBoard().getPiece(start);
        if (playerColor == null) {
            throw new UnauthorizedException("Error: non-player attempted to move piece");
        }
        if (!playerColor.equals(movingPiece.getTeamColor())) {
            throw new UnauthorizedException("Error: it is not the player's piece");
        }
    }

}
