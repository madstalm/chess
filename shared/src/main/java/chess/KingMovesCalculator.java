package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator extends PieceMovesCalculator {

    public Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessPosition> proposedSquares = new ArrayList<>();
        proposedSquares = getProposed(myPosition);
        return kMoves(board, myPosition, proposedSquares);
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

}
