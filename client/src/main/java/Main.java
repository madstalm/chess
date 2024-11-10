import chess.*;
import ui.DrawBoard;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        ChessGame game = new ChessGame();
        DrawBoard printer = new DrawBoard(game);
        //printer.display(ChessGame.TeamColor.BLACK);
        printer.display(ChessGame.TeamColor.WHITE);
    }
}