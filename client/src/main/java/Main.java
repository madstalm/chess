import chess.*;
import ui.*;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        /*//test printing the board
        ChessGame game = new ChessGame();
        DrawBoard printer = new DrawBoard(game);
        printer.display(ChessGame.TeamColor.BLACK);
        */
        var serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        new PreloginUi(serverUrl).run();
    }
}