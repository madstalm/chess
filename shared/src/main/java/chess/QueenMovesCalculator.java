package chess;

import java.util.ArrayList;
import java.util.Collection;

public class QueenMovesCalculator extends PieceMovesCalculator {
    public Collection<ChessMove> queenMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece.PieceType promotionPiece = null;
        Collection<ChessMove> moves = new ArrayList<>();
        Collection<ChessPosition> squares = new ArrayList<>();
        squares = scanQuad(board, myPosition, myPosition, squares, 1);
        squares = scanQuad(board, myPosition, myPosition, squares, 2);
        squares = scanQuad(board, myPosition, myPosition, squares, 3);
        squares = scanQuad(board, myPosition, myPosition, squares, 4);
        squares = scanCompass(board, myPosition, myPosition, squares, Direction.UP);
        squares = scanCompass(board, myPosition, myPosition, squares, Direction.DOWN);
        squares = scanCompass(board, myPosition, myPosition, squares, Direction.LEFT);
        squares = scanCompass(board, myPosition, myPosition, squares, Direction.RIGHT);
        for (ChessPosition square : squares) {
            moves.add(new ChessMove(myPosition, square, promotionPiece));
        }
        return moves;
    }

}
