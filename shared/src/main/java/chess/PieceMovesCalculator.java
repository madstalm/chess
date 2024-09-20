package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMovesCalculator {

     public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece.PieceType type = (board.getPiece(myPosition)).getPieceType();
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        switch (type) {
            case BISHOP:
                BishopMovesCalculator calculator = new BishopMovesCalculator();
                possibleMoves = calculator.bishopMoves(board, myPosition);
                break;
            /* 
            case KING:
                throw new RuntimeException("Not implemented");
                break;  
            case KNIGHT:
                throw new RuntimeException("Not implemented");
                break; 
            case PAWN:
                throw new RuntimeException("Not implemented");
                break;
            case QUEEN:
                throw new RuntimeException("Not implemented");
                break;
            case ROOK:
                throw new RuntimeException("Not implemented");
                break;
            */ 
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

    public static boolean friendOrFoe(ChessBoard board, ChessPosition proposed_position, ChessPosition myPosition) {
        ChessPiece myPiece = board.getPiece(myPosition);
        ChessGame.TeamColor myTeam = myPiece.getTeamColor();
        ChessPiece proposedFoe = board.getPiece(proposed_position);
        if (proposedFoe == null) {
            return true;
        }
        ChessGame.TeamColor proposedTeam = proposedFoe.getTeamColor();
        if (proposedTeam == myTeam) {
            return false;
        }
        else {
            return true;
        }
    }
}
