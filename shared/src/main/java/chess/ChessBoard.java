package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {
        
    }

    /**
     * deep copy constructor for ChessBoard class
     * 
     * @param copy  the board you want a copy of
     */
    public ChessBoard(ChessBoard copy) {
        squares = new ChessPiece[8][8]; //create a new squares array to hold copied pieces
        for (int row = 1; row <= 8; ++row) {
            for (int col = 1; col <= 8; ++col) {
                if (copy.squares[row - 1][col - 1] != null) {
                    ChessPosition position = new ChessPosition(row, col);
                    squares[row - 1][col - 1] = new ChessPiece(copy.getPiece(position).getTeamColor(), copy.getPiece(position).getPieceType());
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true; // Checks if the current object is compared to itself
        if (o == null || getClass() != o.getClass()) return false; // Check if the passed object is of the correct type
        ChessBoard that = (ChessBoard) o; // Typecast the object to ChessBoard
        return Arrays.deepEquals(squares, that.squares); // Compare  the important fields
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(squares); // Generate hash code using relevant fields
    }

    @Override
    public String toString() { 
        return "|" + squares[7][0] + "|" +squares[7][1] + "|" +squares[7][2] + "|" +squares[7][3] + "|" +squares[7][4] + "|" +squares[7][5] + "|" +squares[7][6] + "|" +squares[7][7] + "|\n" +
            "|" + squares[6][0] + "|" +squares[6][1] + "|" +squares[6][2] + "|" +squares[6][3] + "|" +squares[6][4] + "|" +squares[6][5] + "|" +squares[6][6] + "|" +squares[6][7] + "|\n" +
            ". . . . . . . . .\n" +
            "|" + squares[1][0] + "|" +squares[1][1] + "|" +squares[1][2] + "|" +squares[1][3] + "|" +squares[1][4] + "|" +squares[1][5] + "|" +squares[1][6] + "|" +squares[1][7] + "|\n" +
            "|" + squares[0][0] + "|" +squares[0][1] + "|" +squares[0][2] + "|" +squares[0][3] + "|" +squares[0][4] + "|" +squares[0][5] + "|" +squares[0][6] + "|" +squares[0][7] + "|";
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow() - 1][position.getColumn() - 1] = piece;
    }

    /**
     * Removes a chess piece from the chessboard and resets it to null
     *
     * @param position where to remove the piece from
     */
    public void removePiece(ChessPosition position) {
        squares[position.getRow() - 1][position.getColumn() - 1] = null;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        //create pawns
        ChessPiece whitePawn = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        ChessPiece blackPawn = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        //set up pawns
        ChessPosition whiteP = null;
        ChessPosition blackP = null;
        for (int i = 1; i <= 8; ++i) {
            whiteP = new ChessPosition(2, i);
            blackP = new ChessPosition(7, i);
            addPiece(whiteP, whitePawn);
            addPiece(blackP, blackPawn);
        }
        //create rooks
        ChessPiece whiteRook = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK);
        ChessPiece blackRook = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK);
        //set up rooks
        ChessPosition whiteR_L = new ChessPosition(1, 1);
        ChessPosition whiteR_R = new ChessPosition(1, 8);
        ChessPosition blackR_L = new ChessPosition(8, 1);
        ChessPosition blackR_R = new ChessPosition(8, 8);
        addPiece(whiteR_L, whiteRook);
        addPiece(whiteR_R, whiteRook);
        addPiece(blackR_L, blackRook);
        addPiece(blackR_R, blackRook);
        //create knights
        ChessPiece whiteKnight = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT);
        ChessPiece blackKnight = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT);
        //set up knights
        ChessPosition whiteKN_L = new ChessPosition(1, 2);
        ChessPosition whiteKN_R = new ChessPosition(1, 7);
        ChessPosition blackKN_L = new ChessPosition(8, 2);
        ChessPosition blackKN_R = new ChessPosition(8, 7);
        addPiece(whiteKN_L, whiteKnight);
        addPiece(whiteKN_R, whiteKnight);
        addPiece(blackKN_L, blackKnight);
        addPiece(blackKN_R, blackKnight);
        //create bishops
        ChessPiece whiteBishop = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP);
        ChessPiece blackBishop = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);
        //set up bishops
        ChessPosition whiteB_L = new ChessPosition(1, 3);
        ChessPosition whiteB_R = new ChessPosition(1, 6);
        ChessPosition blackB_L = new ChessPosition(8, 3);
        ChessPosition blackB_R = new ChessPosition(8, 6);
        addPiece(whiteB_L, whiteBishop);
        addPiece(whiteB_R, whiteBishop);
        addPiece(blackB_L, blackBishop);
        addPiece(blackB_R, blackBishop);
        //create queens
        ChessPiece whiteQueen = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN);
        ChessPiece blackQueen = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN);
        //set up queens
        ChessPosition whiteQ = new ChessPosition(1, 4);
        ChessPosition blackQ = new ChessPosition(8, 4);
        addPiece(whiteQ, whiteQueen);
        addPiece(blackQ, blackQueen);
        //create kings
        ChessPiece whiteKing = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING);
        ChessPiece blackKing = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING);
        //set up kings
        ChessPosition whiteK = new ChessPosition(1, 5);
        ChessPosition blackK = new ChessPosition(8, 5);
        addPiece(whiteK, whiteKing);
        addPiece(blackK, blackKing);
    }
}
