package br.ldamd;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        try (var scanner = new Scanner(System.in)) {

            // Prompt the user to provide their name
            System.out.println("Please provide your name:");
            var userName = scanner.nextLine();

            // Prompt the user to provide the room number
            System.out.println("Please provide the room number:");
            int roomNumber;

            // Parse the room number from the user input
            try {
                roomNumber = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.err.println("Invalid room number.");
                System.exit(1);
                return;
            }

            // Create a ChatApp instance with the provided username
            var chatApp = new ChatApp(userName);

            // Enter the specified chat room
            chatApp.enterRoom(roomNumber);

            // Start receiving packets asynchronously
            chatApp.receivePacketsAsync();

            // Add a shutdown hook to leave the room when the application is terminated
            Runtime.getRuntime().addShutdownHook(new Thread(chatApp::leaveRoom));

            // Continuously read messages from the user and send them to the chat room
            while (chatApp.isConnected()) {
                var message = scanner.nextLine();
                chatApp.sendMessage(message);
            }

        }

    }

}