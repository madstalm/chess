package chess;

import java.util.Collection;
import java.util.Objects;
import java.util.ArrayList;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    
    private ChessBoard game = new ChessBoard();
    private TeamColor turn;

    public ChessGame() {
        game.resetBoard();
        turn = TeamColor.WHITE;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame that = (ChessGame) o;
        return Objects.equals(game, that.game) && turn == that.turn; 
    }

    @Override
    public int hashCode() {
        return Objects.hash(game, turn);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }


    /**
     * uses setTeamTurn() to change class variable turn to the opposite of what is was set to
     *
     * @param team the team whose turn it is
     */
    private void changeTurn(TeamColor team) {
        switch (team) {
            case WHITE:
                setTeamTurn(TeamColor.BLACK);
                break;
            case BLACK:
                setTeamTurn(TeamColor.WHITE);
                break;
        }
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> potentialMoves = new ArrayList<>();
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece currentPiece = game.getPiece(startPosition);
        if (currentPiece != null) { //if no piece at the start position, validMoves returns an empty array (null)
            potentialMoves.addAll(currentPiece.pieceMoves(game, startPosition));
            ChessBoard ogBoard = getBoard();
            for (ChessMove move : potentialMoves) {
                ChessBoard copyBoard = new ChessBoard(game);
                setBoard(copyBoard);
                tryMove(move);
                //System.out.println(game);
                if (!isInCheck(currentPiece.getTeamColor())) {
                    validMoves.add(move);
                }
                setBoard(ogBoard);
            }
        }
        return validMoves;
    }



    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     * A move is illegal if the chess piece cannot move there, if the move leaves the team’s king in danger, or if it’s not the corresponding team's turn.
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece myPiece = game.getPiece(move.getStartPosition());
        if (myPiece != null) {
            if (myPiece.getTeamColor() == getTeamTurn()) { //throws an error if the move is made on the wrong turn
                if (validMoves(move.getStartPosition()).contains(move)) { //throws an error if the move isn't in validMoves
                    game.removePiece(move.getEndPosition());
                    ChessPiece.PieceType promotionPiece = move.getPromotionPiece();
                    if (promotionPiece != null) { //case for promoting pawns: a promotionPiece exists
                        game.addPiece(move.getEndPosition(), new ChessPiece(getTeamTurn(), promotionPiece));
                    }
                    else {
                        game.addPiece(move.getEndPosition(), myPiece);
                    }
                    game.removePiece(move.getStartPosition());
                    changeTurn(myPiece.getTeamColor());
                }
                else {
                    throw new InvalidMoveException("King put in danger");
                }
            }
            else { throw new InvalidMoveException("Attempted move when not on turn"); }
        }
    else { throw new InvalidMoveException("No piece at start position"); }
    }

    /**
     * 
     * like makeMove, but you pay for your mistakes after, not before
     * 
     * @param move  the move you are trying to make
     * @param board the board (presumably a copy) that you are trying to make it on
     */
    private void tryMove(ChessMove move) {
        ChessPiece myPiece = game.getPiece(move.getStartPosition());
        game.removePiece(move.getEndPosition());
        ChessPiece.PieceType promotionPiece = move.getPromotionPiece();
        if (promotionPiece != null) { //case for promoting pawns: a promotionPiece exists
            game.addPiece(move.getEndPosition(), new ChessPiece(getTeamTurn(), promotionPiece));
        }
        else {
            game.addPiece(move.getEndPosition(), myPiece);
        }
        game.removePiece(move.getStartPosition());
    }


    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck (TeamColor teamColor) {
        boolean checked = false;
        ChessPosition myKing = findKing(teamColor);
        Collection<ChessMove> opponentMoves = new ArrayList<>();
        Collection<ChessPosition> opponents = findOpponents(teamColor);
        for (ChessPosition position : opponents) {
            opponentMoves.addAll(game.getPiece(position).pieceMoves(game, position));
        }
        for (ChessMove move : opponentMoves) {
            if (move.getEndPosition().equals(myKing)) { //use .equals()!!!! not ==
                checked = true;
            }
        }
        return checked;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        game = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return game;
    }

    /**
     * returns the ChessPosition of the king belonging to the team specified by teamColor
     * 
     * @param teamColor Specifies the team whose king we are looking for 
     * @return          ChessPosition where teamColor's king is (or null if it doesn't find the king for some stupid reason)
     */
    private ChessPosition findKing(TeamColor teamColor) {
        ChessPiece kingPiece = new ChessPiece(teamColor, ChessPiece.PieceType.KING);
        for (int row = 1; row <= 8; ++row) {
            for (int col = 1; col <= 8; ++col) {
                ChessPosition scan = new ChessPosition(row, col);
                ChessPiece pieceAtScan = game.getPiece(scan);
                if (pieceAtScan != null) {
                    if (pieceAtScan.equals(kingPiece)) {
                        return scan;
                    }
                }
            }
        }
        return null;
    }


    /**
     * returns a collection of your opponents positions
     * 
     * @param teamColor your teams color
     * @return the ChessPositions of your opponents pieces
     */
    private Collection<ChessPosition> findOpponents(TeamColor teamColor) {
        Collection<ChessPosition> opponents = new ArrayList<>();
        TeamColor opponentColor = null;
        switch (teamColor) {
            case WHITE:
                opponentColor = TeamColor.BLACK;
                break;
            case BLACK:
                opponentColor = TeamColor.WHITE;
                break;
        }
        for (int row = 1; row <= 8; ++row) {
            for (int col = 1; col <= 8; ++col) {
                ChessPosition scan = new ChessPosition(row, col);
                ChessPiece pieceAtScan = game.getPiece(scan);
                if (pieceAtScan != null) {
                    if (pieceAtScan.getTeamColor() == opponentColor) {
                        opponents.add(scan);
                    }
                }
            }
        }
        return opponents;
    }
}
