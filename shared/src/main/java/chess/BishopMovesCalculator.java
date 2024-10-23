package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator extends PieceMovesCalculator {

    public Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece.PieceType promotionPiece = null;
        Collection<ChessMove> moves = new ArrayList<>();
        Collection<ChessPosition> squares = new ArrayList<>();
        squares = scanCompass(board, myPosition, myPosition, squares, Direction.QUAD1); //Q1 squares
        squares = scanCompass(board, myPosition, myPosition, squares, Direction.QUAD2);
        squares = scanCompass(board, myPosition, myPosition, squares, Direction.QUAD3);
        squares = scanCompass(board, myPosition, myPosition, squares, Direction.QUAD4);
        for (ChessPosition square : squares) {
            moves.add(new ChessMove(myPosition, square, promotionPiece));
        }
        return moves;
    }

} 
