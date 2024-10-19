import chess.*;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);
        if (args.length >= 2 && args[1].equals("sql")) {
            boolean startWithSQL = true;
            server.Server testServer = new server.Server(startWithSQL);
            testServer.run(8080);
        }
        else {
            server.Server testServer = new server.Server();
            testServer.run(8080);
        }
    }
}