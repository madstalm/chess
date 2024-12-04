package server.serverwebsocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.io.IOException;

import org.eclipse.jetty.websocket.api.*;

import websocket.commands.*;
import websocket.messages.*;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = new Gson().fromJson(message, UserGameCommand.class);
        //check authorization for the command with the authtoken
        switch (command.getCommandType()) {
            case CONNECT -> connect();
            case MAKE_MOVE -> makeMove();
            case LEAVE -> leave();
            case RESIGN -> resign();
        }
    }

    //these will all likely throw exceptions
    private void connect() {

    }

    private void makeMove() {

    }

    private void leave() {

    }

    private void resign() {

    }

}
