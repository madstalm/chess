package chess;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator extends PieceMovesCalculator {
    public Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece.PieceType promotionPiece = null;
        Collection<ChessMove> moves = new ArrayList<>();
        Collection<ChessPosition> squares = new ArrayList<>();
        squares = scanUp(board, myPosition, myPosition, squares);
        squares = scanDown(board, myPosition, myPosition, squares);
        squares = scanRight(board, myPosition, myPosition, squares);
        squares = scanLeft(board, myPosition, myPosition, squares);
        for (ChessPosition square : squares) {
            moves.add(new ChessMove(myPosition, square, promotionPiece));
        }
        return moves;
    }

}
