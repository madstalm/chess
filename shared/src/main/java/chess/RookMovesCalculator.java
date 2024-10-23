package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator extends PieceMovesCalculator {
    public Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece.PieceType promotionPiece = null;
        Collection<ChessMove> moves = new ArrayList<>();
        Collection<ChessPosition> squares = new ArrayList<>();
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
