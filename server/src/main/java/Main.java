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
            //if I wanted to, I could specify args[0] with a port or something like that, unless that is the function call itself
            server.Server testServer = new server.Server();
            testServer.run(8080);
        }
    }
}