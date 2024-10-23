package chess;

import java.util.ArrayList;
import java.util.Collection;

public class KnightMovesCalculator extends PieceMovesCalculator {
    
    public Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessPosition> proposedSquares = new ArrayList<>();
        proposedSquares = getProposed(myPosition);
        return kMoves(board, myPosition, proposedSquares);
    }

    private static Collection<ChessPosition> getProposed(ChessPosition myPosition) {
        Collection<ChessPosition> squares = new ArrayList<>();
        //quadrant 1
        int row = myPosition.getRow() + 1;
        int col = myPosition.getColumn() + 2;
        squares.add(new ChessPosition(row, col));
        row = myPosition.getRow() + 2;
        col = myPosition.getColumn() + 1;
        squares.add(new ChessPosition(row, col));
        //quadrant 2
        row = myPosition.getRow() + 1;
        col = myPosition.getColumn() - 2;
        squares.add(new ChessPosition(row, col));
        row = myPosition.getRow() + 2;
        col = myPosition.getColumn() - 1;
        squares.add(new ChessPosition(row, col));
        //quadrant 3
        row = myPosition.getRow() - 2;
        col = myPosition.getColumn() - 1;
        squares.add(new ChessPosition(row, col));
        row = myPosition.getRow() - 1;
        col = myPosition.getColumn() - 2;
        squares.add(new ChessPosition(row, col));
        //quadrant 4
        row = myPosition.getRow() - 1;
        col = myPosition.getColumn() + 2;
        squares.add(new ChessPosition(row, col));
        row = myPosition.getRow() - 2;
        col = myPosition.getColumn() + 1;
        squares.add(new ChessPosition(row, col));

        return squares;
    }

}
