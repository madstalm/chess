package server.serverwebsocket;

import org.eclipse.jetty.websocket.api.Session;

import com.google.gson.Gson;

import websocket.messages.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String authToken, Integer gameID, Session session) {
        var connection = new Connection(authToken, gameID, session);
        connections.put(authToken, connection);
    }

    public void remove(String authToken) {
        connections.remove(authToken);
    }

    //might need some kind of broadcast method for game updates that really does blast everyone, not just the excluded user
    public void broadcast(String excludeToken, Integer currentGameID, ServerMessage message) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var c : connections.values()) {
            if (c.session.isOpen()) {
                if ((!c.authToken.equals(excludeToken))&&(c.gameID.equals(currentGameID))) {
                    c.send(new Gson().toJson(message));
                }
            } else {
                removeList.add(c);
            }
        }

        // Clean up any connections that were left open.
        for (var c : removeList) {
            connections.remove(c.authToken);
        }
    }

    public void broadcastUser(String token, ServerMessage message) throws IOException {
        var c = connections.get(token);
        if (c.session.isOpen()) {
            c.send(new Gson().toJson(message));
        }
        else {
            connections.remove(c.authToken);
        }
    }

}
