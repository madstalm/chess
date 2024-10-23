package server;

public record ListGamesResponse(Integer gameID, String whiteUsername, String blackUsername, String gameName) {

}
