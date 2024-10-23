package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMovesCalculator {

     public static Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece.PieceType type = (board.getPiece(myPosition)).getPieceType();
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        switch (type) {
            case BISHOP:
                BishopMovesCalculator bCalculator = new BishopMovesCalculator();
                possibleMoves = bCalculator.bishopMoves(board, myPosition);
                break; 
            case KING:
                KingMovesCalculator kCalculator = new KingMovesCalculator();
                possibleMoves = kCalculator.kingMoves(board, myPosition);
                break;  
            case KNIGHT:
                KnightMovesCalculator knCalculator = new KnightMovesCalculator();
                possibleMoves = knCalculator.knightMoves(board, myPosition);
                break;
            case PAWN:
                PawnMovesCalculator pCalculator = new PawnMovesCalculator();
                possibleMoves = pCalculator.pawnMoves(board, myPosition);
                break;
            case QUEEN:
                QueenMovesCalculator qCalculator = new QueenMovesCalculator();
                possibleMoves = qCalculator.queenMoves(board, myPosition);
                break;
            case ROOK:
                RookMovesCalculator rCalculator = new RookMovesCalculator();
                possibleMoves = rCalculator.rookMoves(board, myPosition);
                break;
             
        }
        return possibleMoves;
    }

    //returns true if move is out of bounds
    public static boolean moveOutofBounds(ChessPosition proposedPosition) {
        int row = proposedPosition.getRow();
        int col = proposedPosition.getColumn();

        if ((row > 8) || (col > 8) || (row < 1) || (col < 1)) {
            return true;
        }
        else {
            return false;
        }
    }

    //returns true if a piece is allowed to move there; square contains enemy
    public static boolean friendOrFoe(ChessBoard board, ChessPosition proposedPosition, ChessPosition myPosition) {
        ChessPiece myPiece = board.getPiece(myPosition);
        ChessGame.TeamColor myTeam = myPiece.getTeamColor();
        ChessPiece proposedFoe = board.getPiece(proposedPosition);
        ChessGame.TeamColor proposedTeam = proposedFoe.getTeamColor();
        if (proposedTeam == myTeam) {
            return false;
        }
        else {
            return true;
        }
    }

    //returns true if there is a piece in the proposed position
    public static boolean spaceOccupied(ChessBoard board, ChessPosition proposedPosition) {
        ChessPiece proposedFoe = board.getPiece(proposedPosition);
        if (proposedFoe == null) {
            return false;
        }
        else { return true;
        }
    }

    //Diagonal Scanner
    public static Collection<ChessPosition> scanQuad(ChessBoard board, ChessPosition myPosition,
            ChessPosition originalPosition, Collection<ChessPosition> moves, int quad) {
        
        int rMod = 0;
        int cMod = 0;
        switch (quad) {
            case 1:
                rMod = 1;
                cMod = 1;
                break;
            case 2:
                rMod = 1;
                cMod = -1;
                break;
            case 3:
                rMod = -1;
                cMod = -1;
                break;
            case 4:
                rMod = -1;
                cMod = 1;
                break;
        }
        int row = myPosition.getRow() + rMod;
        int col = myPosition.getColumn() + cMod;
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
            moves = scanQuad(board, next, originalPosition, moves, quad);
        }
        return moves;
    }

    //Compass Scanner
    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
    
    public static Collection<ChessPosition> scanCompass(ChessBoard board, ChessPosition myPosition,
            ChessPosition originalPosition, Collection<ChessPosition> moves, Direction direction) {
        
        int rMod = 0;
        int cMod = 0;
        switch (direction) {
             case UP:
                rMod = 1;
                break;
            case DOWN:
                rMod = -1;
                break;
            case LEFT:
                cMod = -1;
                break;
            case RIGHT:
                cMod = 1;
                break;
        }
        int row = myPosition.getRow() + rMod;
        int col = myPosition.getColumn() + cMod;
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
            moves = scanCompass(board, next, originalPosition, moves, direction);
        }
        return moves;
    }

}
