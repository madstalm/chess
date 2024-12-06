package ui;

import java.util.ArrayList;
import java.util.Scanner;
import websocket.messages.*;
import com.google.gson.Gson;

import static ui.EscapeSequences.*;

public class UiManager implements ServerMessageHandler {
    private final Client client;
    
    public UiManager(String serverUrl) {
        client = new Client(serverUrl, this);
    }

    public void run() {
        System.out.println("Welcome to the 240 " + WHITE_KING + "chess" + BLACK_KING +
                "client.\nPlease type in an option from below to begin.");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = client.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        scanner.close();
        System.out.print(RESET_TEXT_COLOR);
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_GREEN);
    }
    
    public void notify(String message) {
        ServerMessage unprocessed = new Gson().fromJson(message, ServerMessage.class);
        switch (unprocessed.getServerMessageType()) {
            case NOTIFICATION:
                NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
                System.out.println(SET_TEXT_COLOR_BLUE + notification.getNotification());
                break;
            case ERROR:
                ErrorMessage errorMessage = new Gson().fromJson(message, ErrorMessage.class);
                System.out.println(SET_TEXT_COLOR_RED + errorMessage.getErrorMessage());
                break;
            case LOAD_GAME:
                LoadGameMessage loadGame = new Gson().fromJson(message, LoadGameMessage.class);
                client.setGame(loadGame.getChessGame());
                DrawBoard artist = new DrawBoard(loadGame.getChessGame(), new ArrayList<>());
                System.out.print(artist.display(client.getPlayerColor()) + "\n");
                break;
            default:
                System.out.println("Error: unexpected message behavior");
                break;
        }
        printPrompt();
    }
}
