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


    private static Collection<ChessPosition> scanQuad1(ChessBoard board, ChessPosition myPosition, ChessPosition originalPosition, Collection<ChessPosition> moves) {
        int row = myPosition.getRow() + 1;
        int col = myPosition.getColumn() + 1;
        ChessPosition next = new ChessPosition(row, col);
        boolean outOfBounds = moveOutofBounds(next);
        if (outOfBounds) {
            return moves;
        }
        moves = scanQuad1(board, next, originalPosition, moves);
        boolean friendOrFoe = friendOrFoe(board, next, originalPosition);
        if (friendOrFoe) {
            moves.add(next);
        }
        return moves;
    }


    private static Collection<ChessPosition> scanQuad2(ChessBoard board, ChessPosition myPosition, ChessPosition originalPosition, Collection<ChessPosition> moves) {
        int row = myPosition.getRow() + 1;
        int col = myPosition.getColumn() - 1;
        ChessPosition next = new ChessPosition(row, col);
        boolean outOfBounds = moveOutofBounds(next);
        if (outOfBounds) {
            return moves;
        }
        moves = scanQuad2(board, next, originalPosition, moves);
        boolean friendOrFoe = friendOrFoe(board, next, originalPosition);
        if (friendOrFoe) {
            moves.add(next);
        }
        return moves;
    }


    private static Collection<ChessPosition> scanQuad3(ChessBoard board, ChessPosition myPosition, ChessPosition originalPosition, Collection<ChessPosition> moves) {
        int row = myPosition.getRow() - 1;
        int col = myPosition.getColumn() - 1;
        ChessPosition next = new ChessPosition(row, col);
        boolean outOfBounds = moveOutofBounds(next);
        if (outOfBounds) {
            return moves;
        }
        moves = scanQuad3(board, next, originalPosition, moves);
        boolean friendOrFoe = friendOrFoe(board, next, originalPosition);
        if (friendOrFoe) {
            moves.add(next);
        }
        return moves;
    }


    private static Collection<ChessPosition> scanQuad4(ChessBoard board, ChessPosition myPosition, ChessPosition originalPosition, Collection<ChessPosition> moves) {
        int row = myPosition.getRow() - 1;
        int col = myPosition.getColumn() + 1;
        ChessPosition next = new ChessPosition(row, col);
        boolean outOfBounds = moveOutofBounds(next);
        if (outOfBounds) {
            return moves;
        }
        moves = scanQuad4(board, next, originalPosition, moves);
        boolean friendOrFoe = friendOrFoe(board, next, originalPosition);
        if (friendOrFoe) {
            moves.add(next);
        }
        return moves;
    }
} 
