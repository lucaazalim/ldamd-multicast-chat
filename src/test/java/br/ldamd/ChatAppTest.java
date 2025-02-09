package br.ldamd;

import br.ldamd.exceptions.AlreadyConnectedToARoomException;
import br.ldamd.exceptions.NotConnectedToARoomException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ChatApp class.
 */
public class ChatAppTest {

    private ChatApp chatApp;

    /**
     * Cleans up after each test by leaving the room if connected.
     */
    @AfterEach
    public void tearDown() {
        if (chatApp != null && chatApp.isConnected()) {
            chatApp.leaveRoom();
        }
    }

    /**
     * Tests that entering a room sets the connected status to true.
     */
    @Test
    public void testEnterRoomSetsConnected() {
        chatApp = new ChatApp("testUser");
        assertFalse(chatApp.isConnected(), "Should not be connected initially.");

        chatApp.enterRoom(1);
        assertTrue(chatApp.isConnected(), "Should be connected after entering a room.");
    }

    /**
     * Tests that entering a room twice throws an AlreadyConnectedToARoomException.
     */
    @Test
    public void testEnterRoomTwiceThrows() {
        chatApp = new ChatApp("testUser");
        chatApp.enterRoom(1);

        assertThrows(AlreadyConnectedToARoomException.class, () -> {
            chatApp.enterRoom(2);
        });
    }

    /**
     * Tests that leaving a room when not connected throws a NotConnectedToARoomException.
     */
    @Test
    public void testLeaveRoomWhenNotConnectedThrows() {
        chatApp = new ChatApp("testUser");
        assertThrows(NotConnectedToARoomException.class, chatApp::leaveRoom);
    }

    /**
     * Tests that sending a message when not connected throws a NotConnectedToARoomException.
     */
    @Test
    public void testSendMessageWhenNotConnectedThrows() {
        chatApp = new ChatApp("testUser");
        assertThrows(NotConnectedToARoomException.class, () -> chatApp.sendMessage("Hello!"));
    }

    /**
     * Tests that sending a message when connected does not throw any exceptions.
     */
    @Test
    public void testSendMessageSendsSuccessfully() {
        chatApp = new ChatApp("testUser");
        chatApp.enterRoom(1);
        assertDoesNotThrow(() -> chatApp.sendMessage("Hello!"));
    }

    /**
     * Tests that receiving packets asynchronously does not block the main thread
     * and that the PacketReceiver thread is running.
     */
    @Test
    public void testReceivePacketsAsyncDoesNotBlock() throws InterruptedException {
        chatApp = new ChatApp("testUser");
        chatApp.enterRoom(1);
        chatApp.receivePacketsAsync();

        Thread.sleep(100);

        assertTrue(Thread.getAllStackTraces().keySet().stream()
                .anyMatch((thread) -> thread.getName().equals("PacketReceiver"))
        );
    }
}