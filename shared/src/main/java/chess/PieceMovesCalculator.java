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

    //Diagonal Scanners
    public static Collection<ChessPosition> scanQuad1(ChessBoard board, ChessPosition myPosition,
            ChessPosition originalPosition, Collection<ChessPosition> moves) {
        int row = myPosition.getRow() + 1;
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
            moves = scanQuad1(board, next, originalPosition, moves);
        }
        return moves;
    }


    public static Collection<ChessPosition> scanQuad2(ChessBoard board, ChessPosition myPosition,
            ChessPosition originalPosition, Collection<ChessPosition> moves) {
        int row = myPosition.getRow() + 1;
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
            moves = scanQuad2(board, next, originalPosition, moves);
        }
        return moves;
    }


    public static Collection<ChessPosition> scanQuad3(ChessBoard board, ChessPosition myPosition,
            ChessPosition originalPosition, Collection<ChessPosition> moves) {
        int row = myPosition.getRow() - 1;
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
            moves = scanQuad3(board, next, originalPosition, moves);
        }
        return moves;
    }


    public static Collection<ChessPosition> scanQuad4(ChessBoard board, ChessPosition myPosition,
            ChessPosition originalPosition, Collection<ChessPosition> moves) {
        int row = myPosition.getRow() - 1;
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
            moves = scanQuad4(board, next, originalPosition, moves);
        }
        return moves;
    }

    //Compass Scanners
    public static Collection<ChessPosition> scanUp(ChessBoard board, ChessPosition myPosition,
            ChessPosition originalPosition, Collection<ChessPosition> moves) {
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


    public static Collection<ChessPosition> scanDown(ChessBoard board, ChessPosition myPosition,
            ChessPosition originalPosition, Collection<ChessPosition> moves) {
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


    public static Collection<ChessPosition> scanRight(ChessBoard board, ChessPosition myPosition,
            ChessPosition originalPosition, Collection<ChessPosition> moves) {
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


    public static Collection<ChessPosition> scanLeft(ChessBoard board, ChessPosition myPosition,
            ChessPosition originalPosition, Collection<ChessPosition> moves) {
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
