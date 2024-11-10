package ui;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static ui.EscapeSequences.*;
import chess.ChessGame;
import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;

public class DrawBoard {
    private static ChessGame game;
    private static ChessBoard board;

    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;

    // Padded characters.
    private static final String EMPTY = "   ";
    private static final String K = " K ";
    private static final String Q = " Q ";
    private static final String B = " B ";
    private static final String N = " N ";
    private static final String R = " R ";
    private static final String P = " P ";

    public DrawBoard(ChessGame chessGame) {
        game = chessGame;
        board = game.getBoard();
    }

    public static void display(ChessGame.TeamColor pov) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.print(ERASE_SCREEN);

        switch (pov) {
            case WHITE:
                drawAlphaHeaders(out, false);
                drawMiddle(out, false);
                drawAlphaHeaders(out, false);
                break;
            case BLACK:
                drawAlphaHeaders(out, true);
                drawMiddle(out, true);
                drawAlphaHeaders(out, true);
                break;
        }
    }

    private static void drawAlphaHeaders(PrintStream out, boolean flip) {

        setBlack(out);

        String[] headers = { EMPTY, " a ", " b ", " c ", " d ", " e ", " f ", " g ", " h ", EMPTY };
        if (flip) {
            headers = new String[] { EMPTY, " h ", " g ", " f ", " e ", " d ", " c ", " b ", " a ", EMPTY };
        }

        for (int boardCol = 0; boardCol < 10; ++boardCol) {
            drawHeader(out, headers[boardCol]);
        }

        out.println();
    }

    private static void drawHeader(PrintStream out, String headerText) {
        printHeaderText(out, headerText);
    }

    private static void printHeaderText(PrintStream out, String text) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
        out.print(text);
        setBlack(out);
    }

    private static void drawMiddle(PrintStream out, boolean flip) {
        String[] rows = { " 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 " };
        
        if (flip) {
            for (int boardRow = 1; boardRow <= BOARD_SIZE_IN_SQUARES; ++boardRow) {
                printHeaderText(out, rows[boardRow-1]);
                drawRowOfSquares(out, flip, boardRow);
                printHeaderText(out, rows[boardRow-1]);
                out.println();
            }
        }
        else {
            for (int boardRow = BOARD_SIZE_IN_SQUARES; boardRow > 0; --boardRow) {
                printHeaderText(out, rows[boardRow-1]);
                drawRowOfSquares(out, flip, boardRow);
                printHeaderText(out, rows[boardRow-1]);
                out.println();
            }
        }
    }

    private static void drawRowOfSquares(PrintStream out, boolean flip, int row) {
        //true is white squares, this is good for even rows
        boolean[] colors = { true, false, true, false, true, false, true, false };
        if (!isEven(row)) {
            colors = new boolean[] { false, true, false, true, false, true, false, true };
        }
        if (flip) {
            for (int boardCol = BOARD_SIZE_IN_SQUARES; boardCol >= 1; --boardCol) {
                
            }
        }
        else {
            for (int boardCol = 1; boardCol <= BOARD_SIZE_IN_SQUARES; ++boardCol) {
                if (colors[boardCol-1]) {
                    out.print(SET_BG_COLOR_LIGHT_GREY);
                }
                else {
                    out.print(SET_BG_COLOR_BLUE);
                }
                ChessPiece currentPiece = board.getPiece(new ChessPosition(row, boardCol));
                if (currentPiece == null) {
                    out.print(EMPTY);
                }
                else {
                    ChessPiece.PieceType type = currentPiece.getPieceType();
                    ChessGame.TeamColor team = currentPiece.getTeamColor();
                    switch (team) {
                        case WHITE:
                            out.print(SET_TEXT_COLOR_WHITE);
                            break;
                        case BLACK:
                            out.print(SET_TEXT_COLOR_BLACK);
                            break;
                    }
                    switch (type) {
                        case KING:
                            out.print(K);
                            break;
                        case QUEEN:
                            out.print(Q);
                            break;
                        case BISHOP:
                            out.print(B);
                            break;
                        case KNIGHT:
                            out.print(N);
                            break;
                        case ROOK:
                            out.print(R);
                            break;
                        case PAWN:
                            out.print(P);
                            break;
                    }
                } 
            }
        }
        setBlack(out);
        out.println();
    }

    private static boolean isEven(int number) {
        return number % 2 == 0;
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_BLACK);
    }

}
