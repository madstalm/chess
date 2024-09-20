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


    private static Collection<ChessPosition> scanUp(ChessBoard board, ChessPosition myPosition, ChessPosition originalPosition, Collection<ChessPosition> moves) {
        int row = myPosition.getRow() + 1;
        int col = myPosition.getColumn();
        ChessPosition next = new ChessPosition(row, col);
        boolean outOfBounds = moveOutofBounds(next);
        if (outOfBounds) {
            return moves;
        }
        boolean occupied = spaceOccupied(board, next);
        if (occupied) {
            boolean friendOrFoe = friendOrFoe(board, next, originalPosition);
            if (friendOrFoe) {
                moves.add(next);
            }
            else {
                return moves;
            }
        }
        else {
            moves.add(next);
            moves = scanUp(board, next, originalPosition, moves);
        }
        return moves;
    }


    private static Collection<ChessPosition> scanDown(ChessBoard board, ChessPosition myPosition, ChessPosition originalPosition, Collection<ChessPosition> moves) {
        int row = myPosition.getRow() - 1;
        int col = myPosition.getColumn();
        ChessPosition next = new ChessPosition(row, col);
        boolean outOfBounds = moveOutofBounds(next);
        if (outOfBounds) {
            return moves;
        }
        boolean occupied = spaceOccupied(board, next);
        if (occupied) {
            boolean friendOrFoe = friendOrFoe(board, next, originalPosition);
            if (friendOrFoe) {
                moves.add(next);
            }
            else {
                return moves;
            }
        }
        else {
            moves.add(next);
            moves = scanDown(board, next, originalPosition, moves);
        }
        return moves;
    }


    private static Collection<ChessPosition> scanRight(ChessBoard board, ChessPosition myPosition, ChessPosition originalPosition, Collection<ChessPosition> moves) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn() + 1;
        ChessPosition next = new ChessPosition(row, col);
        boolean outOfBounds = moveOutofBounds(next);
        if (outOfBounds) {
            return moves;
        }
        boolean occupied = spaceOccupied(board, next);
        if (occupied) {
            boolean friendOrFoe = friendOrFoe(board, next, originalPosition);
            if (friendOrFoe) {
                moves.add(next);
            }
            else {
                return moves;
            }
        }
        else {
            moves.add(next);
            moves = scanRight(board, next, originalPosition, moves);
        }
        return moves;
    }


    private static Collection<ChessPosition> scanLeft(ChessBoard board, ChessPosition myPosition, ChessPosition originalPosition, Collection<ChessPosition> moves) {
        int row = myPosition.getRow();
        int col = myPosition.getColumn() - 1;
        ChessPosition next = new ChessPosition(row, col);
        boolean outOfBounds = moveOutofBounds(next);
        if (outOfBounds) {
            return moves;
        }
        boolean occupied = spaceOccupied(board, next);
        if (occupied) {
            boolean friendOrFoe = friendOrFoe(board, next, originalPosition);
            if (friendOrFoe) {
                moves.add(next);
            }
            else {
                return moves;
            }
        }
        else {
            moves.add(next);
            moves = scanLeft(board, next, originalPosition, moves);
        }
        return moves;
    }
}
