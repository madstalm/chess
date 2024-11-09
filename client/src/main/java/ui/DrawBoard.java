package ui;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;
import chess.ChessGame;
import chess.ChessBoard;
import chess.ChessPiece;

public class DrawBoard {
    private ChessGame game;
    private ChessBoard board;

    public DrawBoard(ChessGame chessGame) {
        this.game = chessGame;
        this.board = this.game.getBoard();
    }
}
