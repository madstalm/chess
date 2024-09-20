package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator extends PieceMovesCalculator{

    public Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece pawn = board.getPiece(myPosition);
        ChessGame.TeamColor pawnTeam = pawn.getTeamColor();
        switch(pawnTeam) {
            case WHITE:
                moves = whiteMoves(board, myPosition);
                break;
            case BLACK:
                moves = blackMoves(board, myPosition);
                break;
        }
        return moves;
    }

    private static Collection<ChessMove> blackMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        // move one space down
        ChessPosition move1 = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());
        if (!spaceOccupied(board, move1)) {
            if (move1.getRow() == 1) {
                moves.addAll(promotionMoves(myPosition, move1));
            }
            else {
                moves.add(new ChessMove(myPosition, move1, null));
            }
        }
        // move two spaces if first turn
        if (myPosition.getRow() == 7) {
            ChessPosition move2 = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn());
            if ((!spaceOccupied(board, move2)) && (!spaceOccupied(board, move1))) {
                moves.add(new ChessMove(myPosition, move2, null));
            }
        }
        // capture spaces if occupied by enemy
        ChessPosition captureL = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1);
        ChessPosition captureR = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1);
        if (spaceOccupied(board, captureL)) {
            if (friendOrFoe(board, captureL, myPosition)) {
                if (captureL.getRow() == 1) {
                    moves.addAll(promotionMoves(myPosition, captureL));
                }
                else {
                    moves.add(new ChessMove(myPosition, captureL, null));
                }
            }
        }
        if (spaceOccupied(board, captureR)) {
            if (friendOrFoe(board, captureR, myPosition)) {
                if (captureL.getRow() == 1) {
                    moves.addAll(promotionMoves(myPosition, captureR));
                }
                else {
                    moves.add(new ChessMove(myPosition, captureR, null));
                }
            }
        }
        return moves;
    }

    private static Collection<ChessMove> whiteMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        // move one space up
        ChessPosition move1 = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
        if (!spaceOccupied(board, move1)) {
            if (move1.getRow() == 8) {
                moves.addAll(promotionMoves(myPosition, move1));
            }
            else {
                moves.add(new ChessMove(myPosition, move1, null));
            }
        }
        // move two spaces if first turn
        if (myPosition.getRow() == 2) {
            ChessPosition move2 = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn());
            if((!spaceOccupied(board, move2)) && (!spaceOccupied(board, move1))) {
                moves.add(new ChessMove(myPosition, move2, null));
            }
        }
        // capture spaces if occupied by enemy
        ChessPosition captureL = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1);
        ChessPosition captureR = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1);
        if (spaceOccupied(board, captureL)) {
            if (friendOrFoe(board, captureL, myPosition)) {
                if (captureL.getRow() == 8) {
                    moves.addAll(promotionMoves(myPosition, captureL));
                }
                else {
                    moves.add(new ChessMove(myPosition, captureL, null));
                }
            }
        }
        if (spaceOccupied(board, captureR)) {
            if (friendOrFoe(board, captureR, myPosition)) {
                if (captureL.getRow() == 8) {
                    moves.addAll(promotionMoves(myPosition, captureR));
                }
                else {
                    moves.add(new ChessMove(myPosition, captureR, null));
                }
            }
        }
        return moves;
    }

    private static Collection<ChessMove> promotionMoves(ChessPosition myPosition, ChessPosition endPosition) {
        Collection<ChessMove> moves = new ArrayList<>();
        moves.add(new ChessMove(myPosition, endPosition, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(myPosition, endPosition, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(myPosition, endPosition, ChessPiece.PieceType.KNIGHT));
        moves.add(new ChessMove(myPosition, endPosition, ChessPiece.PieceType.BISHOP));
        return moves;
    }
}
