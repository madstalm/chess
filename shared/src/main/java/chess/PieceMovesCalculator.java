package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMovesCalculator {

     public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece.PieceType type = (board.getPiece(myPosition)).getPieceType();
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        switch (type) {
            case BISHOP:
                BishopMovesCalculator Bcalculator = new BishopMovesCalculator();
                possibleMoves = Bcalculator.bishopMoves(board, myPosition);
                break; 
            case KING:
                KingMovesCalculator Kcalculator = new KingMovesCalculator();
                possibleMoves = Kcalculator.kingMoves(board, myPosition);
                break;  
            case KNIGHT:
                KnightMovesCalculator KNcalculator = new KnightMovesCalculator();
                possibleMoves = KNcalculator.knightMoves(board, myPosition);
                break;
            case PAWN:
                PawnMovesCalculator Pcalculator = new PawnMovesCalculator();
                possibleMoves = Pcalculator.pawnMoves(board, myPosition);
                break;
            case QUEEN:
                QueenMovesCalculator Qcalculator = new QueenMovesCalculator();
                possibleMoves = Qcalculator.queenMoves(board, myPosition);
                break;
            case ROOK:
                RookMovesCalculator Rcalculator = new RookMovesCalculator();
                possibleMoves = Rcalculator.rookMoves(board, myPosition);
                break;
             
        }
        return possibleMoves;
    }

    public static boolean moveOutofBounds(ChessPosition proposed_position) {
        int row = proposed_position.getRow();
        int col = proposed_position.getColumn();

        if ((row > 8) || (col > 8) || (row < 1) || (col < 1)) {
            return true;
        }
        else {
            return false;
        }
    }

    //returns true if a piece is allowed to move there; square contains enemy
    public static boolean friendOrFoe(ChessBoard board, ChessPosition proposed_position, ChessPosition myPosition) {
        ChessPiece myPiece = board.getPiece(myPosition);
        ChessGame.TeamColor myTeam = myPiece.getTeamColor();
        ChessPiece proposedFoe = board.getPiece(proposed_position);
        ChessGame.TeamColor proposedTeam = proposedFoe.getTeamColor();
        if (proposedTeam == myTeam) {
            return false;
        }
        else {
            return true;
        }
    }

    //returns true if there is a piece in the proposed position
    public static boolean spaceOccupied(ChessBoard board, ChessPosition proposed_position) {
        ChessPiece proposedFoe = board.getPiece(proposed_position);
        if (proposedFoe == null) {
            return false;
        }
        else { return true;
        }
    }
}
