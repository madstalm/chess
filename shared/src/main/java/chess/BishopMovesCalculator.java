package chess;

import java.util.ArrayList;
import java.util.Collection;

public class BishopMovesCalculator extends PieceMovesCalculator {

    public Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece.PieceType promotionPiece = null;
        Collection<ChessMove> moves = new ArrayList<>();
        Collection<ChessPosition> squares = new ArrayList<>();
        squares = scanQuad1(board, myPosition, myPosition, squares); //Q1 squares
        squares = scanQuad2(board, myPosition, myPosition, squares);
        squares = scanQuad3(board, myPosition, myPosition, squares);
        squares = scanQuad4(board, myPosition, myPosition, squares);
        for (ChessPosition square : squares) {
            moves.add(new ChessMove(myPosition, square, promotionPiece));
        }
        return moves;
    }

} 
