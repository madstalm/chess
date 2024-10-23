package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator extends PieceMovesCalculator {

    public Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece.PieceType promotionPiece = null;
        Collection<ChessMove> moves = new ArrayList<>();
        Collection<ChessPosition> squares = new ArrayList<>();
        Collection<ChessPosition> proposedSquares = new ArrayList<>();
        proposedSquares = getProposed(myPosition);
        squares = validatePositions(board, proposedSquares, myPosition);
        for (ChessPosition square : squares) {
            moves.add(new ChessMove(myPosition, square, promotionPiece));
        }
        return moves;
    }

    private static Collection<ChessPosition> getProposed(ChessPosition myPosition) {
        Collection<ChessPosition> squares = new ArrayList<>();
        //top row
        int row = myPosition.getRow() + 1;
        int col = myPosition.getColumn() - 1;
        squares.add(new ChessPosition(row, col));
        row = myPosition.getRow() + 1;
        col = myPosition.getColumn();
        squares.add(new ChessPosition(row, col));
        row = myPosition.getRow() + 1;
        col = myPosition.getColumn() + 1;
        squares.add(new ChessPosition(row, col));
        //middle row
        row = myPosition.getRow();
        col = myPosition.getColumn() - 1;
        squares.add(new ChessPosition(row, col));
        row = myPosition.getRow();
        col = myPosition.getColumn() + 1;
        squares.add(new ChessPosition(row, col));
        //bottom row
        row = myPosition.getRow() - 1;
        col = myPosition.getColumn() - 1;
        squares.add(new ChessPosition(row, col));
        row = myPosition.getRow() - 1;
        col = myPosition.getColumn();
        squares.add(new ChessPosition(row, col));
        row = myPosition.getRow() - 1;
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
