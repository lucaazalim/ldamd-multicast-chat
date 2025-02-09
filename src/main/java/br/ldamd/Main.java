package br.ldamd;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        try (var scanner = new Scanner(System.in)) {

            System.out.println("Please provide your name:");

            var userName = scanner.nextLine();

            System.out.println("Please provide the room number:");

            int roomNumber;

            try {
                roomNumber = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.err.println("Invalid room number.");
                System.exit(1);
                return;
            }

            var chatApp = new ChatApp(userName);

            chatApp.enterRoom(roomNumber);
            chatApp.receivePacketsAsync();

            Runtime.getRuntime().addShutdownHook(new Thread(chatApp::leaveRoom));

            while (chatApp.isConnected()) {
                var message = scanner.nextLine();
                chatApp.sendMessage(message);
            }

        }

    }

}