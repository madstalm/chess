package ui;

import com.google.gson.Gson;
import chess.*;
import model.*;

import java.io.*;
import java.net.*;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData register(UserData user) throws ClientException {
        var path = "/user";
        return this.makeRequest("POST", path, null, user, AuthData.class);
    }

    public AuthData login(UserData user) throws ClientException {
        var path = "/session";
        return this.makeRequest("POST", path, null, user, AuthData.class);
    }

    public void logout(AuthData token) throws ClientException {
        var path = "/session";
        this.makeRequest("DELETE", path, token, null, null);
    }

    public GameData[] listGames(AuthData token) throws ClientException {
        var path = "/game";
        record ListGamesResponse(GameData[] games) {
        }
        var response = this.makeRequest("GET", path, token, null, ListGamesResponse.class);
        return response.games();
    }

    public int createGame(GameData game, AuthData token) throws ClientException {
        var path = "/game";
        record CreateGameResponse(int gameID) {
        }
        if ((game.gameName() == null) || (game.gameName() == "")) {
            throw new ClientException("no gameName specified");
        }
        var response = this.makeRequest("POST", path, token, game, CreateGameResponse.class);
        return response.gameID();
    }

    public void joinGame(JoinGameRequest game, AuthData token) throws ClientException {
        var path = "/game";
        this.makeRequest("PUT", path, token, game, null);
    }


    private <T> T makeRequest(String method, String path, AuthData token, Object request, Class<T> responseClass) throws ClientException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            if (token != null) {
                http.addRequestProperty("authorization", token.authToken());
            }

            writeBody(request, http);
            http.setReadTimeout(5000);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception e) {
            throw new ClientException(e.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ClientException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ClientException("failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
