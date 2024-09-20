package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator extends PieceMovesCalculator {
    
    public Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece.PieceType promotionPiece = null;
        Collection<ChessMove> moves = new ArrayList<>();
        Collection<ChessPosition> squares = new ArrayList<>();
        Collection<ChessPosition> proposed_squares = new ArrayList<>();
        proposed_squares = getProposed(myPosition);
        squares = validatePositions(board, proposed_squares, myPosition);
        for (ChessPosition square : squares) {
            moves.add(new ChessMove(myPosition, square, promotionPiece));
        }
        return moves;
    }

    private static Collection<ChessPosition> getProposed(ChessPosition myPosition) {
        Collection<ChessPosition> squares = new ArrayList<>();
        //quadrant 1
        int row = myPosition.getRow() + 1;
        int col = myPosition.getColumn() + 2;
        squares.add(new ChessPosition(row, col));
        row = myPosition.getRow() + 2;
        col = myPosition.getColumn() + 1;
        squares.add(new ChessPosition(row, col));
        //quadrant 2
        row = myPosition.getRow() + 1;
        col = myPosition.getColumn() - 2;
        squares.add(new ChessPosition(row, col));
        row = myPosition.getRow() + 2;
        col = myPosition.getColumn() - 1;
        squares.add(new ChessPosition(row, col));
        //quadrant 3
        row = myPosition.getRow() - 2;
        col = myPosition.getColumn() - 1;
        squares.add(new ChessPosition(row, col));
        row = myPosition.getRow() - 1;
        col = myPosition.getColumn() - 2;
        squares.add(new ChessPosition(row, col));
        //quadrant 4
        row = myPosition.getRow() - 1;
        col = myPosition.getColumn() + 2;
        squares.add(new ChessPosition(row, col));
        row = myPosition.getRow() - 2;
        col = myPosition.getColumn() + 1;
        squares.add(new ChessPosition(row, col));

        return squares;
    }

    private static Collection<ChessPosition> validatePositions(ChessBoard board, Collection<ChessPosition> proposed, ChessPosition myPosition) {
        Collection<ChessPosition> squares = new ArrayList<>();
        for (ChessPosition square : proposed) {
            if (!moveOutofBounds(square)) {
                if (!spaceOccupied(board, square)) {
                    squares.add(square);
                }
                else {
                    if (friendOrFoe(board, square, myPosition)) {
                        squares.add(square);
                    }
                }
            }
        }
        return squares;
    }
}
