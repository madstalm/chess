package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator extends PieceMovesCalculator {

    public Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece.PieceType promotionPiece = null;
        Collection<ChessMove> moves = new ArrayList<>();
        Collection<ChessPosition> squares = new ArrayList<>();
        squares = scanQuad(board, myPosition, myPosition, squares, 1); //Q1 squares
        squares = scanQuad(board, myPosition, myPosition, squares, 2);
        squares = scanQuad(board, myPosition, myPosition, squares, 3);
        squares = scanQuad(board, myPosition, myPosition, squares, 4);
        for (ChessPosition square : squares) {
            moves.add(new ChessMove(myPosition, square, promotionPiece));
        }
        return moves;
    }

} 
