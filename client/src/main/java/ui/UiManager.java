package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

public class UiManager {
    private final Client client;
    
    public UiManager(String serverUrl) {
        client = new Client(serverUrl);
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
}
