package br.ldamd;

import br.ldamd.exceptions.AlreadyConnectedToARoomException;
import br.ldamd.exceptions.NotConnectedToARoomException;
import br.ldamd.packets.MessagePacket;
import br.ldamd.packets.UserJoinPacket;
import br.ldamd.packets.UserLeavePacket;

import java.io.IOException;
import java.net.*;

/**
 * The ChatApp class provides functionality for a user to join a chat room, send messages, and receive messages asynchronously.
 */
public class ChatApp {

    private static final String ADDRESS_PREFIX = "230.0.0.";
    private static final int PORT = 6789;
    private static final int BUFFER_SIZE = 1000;

    private final String username;

    private InetAddress groupAddress;
    private InetSocketAddress group;
    private MulticastSocket multicastSocket;

    /**
     * Constructs a ChatApp instance with the specified username.
     *
     * @param name the username of the user
     */
    public ChatApp(String name) {
        username = name;
    }

    /**
     * Enters a chat room with the specified room number.
     *
     * @param roomNumber the room number to join
     * @throws AlreadyConnectedToARoomException if the user is already connected to a room
     */
    public void enterRoom(int roomNumber) {

        if (isConnected()) {
            throw new AlreadyConnectedToARoomException();
        }

        try {
            groupAddress = InetAddress.getByName(ADDRESS_PREFIX + roomNumber);
        } catch (UnknownHostException e) {
            throw new RuntimeException("Unknown host for the specified room number.", e);
        }

        group = new InetSocketAddress(groupAddress, PORT);

        try {

            multicastSocket = new MulticastSocket(PORT);
            multicastSocket.joinGroup(groupAddress);  // Alterado para usar groupAddress diretamente

            UserJoinPacket handshakePacket = new UserJoinPacket(username);  // Definido tipo explicitamente
            sendPacket(handshakePacket);

        } catch (IOException e) {
            throw new RuntimeException("Unable to join the specified room.", e);
        }

    }

    /**
     * Leaves the current chat room.
     *
     * @throws NotConnectedToARoomException if the user is not connected to any room
     */
    public void leaveRoom() {

        if (multicastSocket == null) {
            throw new NotConnectedToARoomException();
        }

        UserLeavePacket handshakePacket = new UserLeavePacket(username);  // Definido tipo explicitamente
        sendPacket(handshakePacket);

        try {
            multicastSocket.leaveGroup(groupAddress);  // Alterado para usar groupAddress diretamente
        } catch (IOException e) {
            throw new RuntimeException("Unable to leave room.", e);
        }

        multicastSocket.close();
        groupAddress = null;
        group = null;

    }

    /**
     * Sends a packet to the current chat room.
     *
     * @param packet the packet to send
     * @throws NotConnectedToARoomException if the user is not connected to any room
     */
    private void sendPacket(Packet packet) {

        if (!isConnected()) {
            throw new NotConnectedToARoomException();
        }

        byte[] serializedPacket;

        try {
            serializedPacket = packet.serialize();
        } catch (IOException e) {
            throw new RuntimeException("Unable to serialize the packet.", e);
        }

        DatagramPacket datagramPacketOut = new DatagramPacket(serializedPacket, serializedPacket.length, groupAddress, PORT);

        try {
            multicastSocket.send(datagramPacketOut);
        } catch (IOException e) {
            throw new RuntimeException("Unable to send packet.", e);
        }

    }

    /**
     * Sends a message to the current chat room.
     *
     * @param messageContent the content of the message to send
     * @throws NotConnectedToARoomException if the user is not connected to any room
     */
    public void sendMessage(String messageContent) {

        MessagePacket message = new MessagePacket(username, messageContent);  // Definido tipo explicitamente
        sendPacket(message);

    }

    /**
     * Receives packets from the current chat room.
     */
    public void receivePackets() {

        byte[] buffer = new byte[BUFFER_SIZE];

        while (isConnected()) {

            DatagramPacket datagramPacketIn = new DatagramPacket(buffer, BUFFER_SIZE);

            try {
                multicastSocket.receive(datagramPacketIn);
            } catch (IOException e) {
                throw new RuntimeException("Unable to receive message.", e);
            }

            Packet receivedPacket;

            try {
                receivedPacket = Packet.deserialize(datagramPacketIn.getData());
            } catch (ClassNotFoundException | IOException e) {
                throw new RuntimeException("Unable to deserialize packet.", e);
            }

            receivedPacket.onReceive();
        }

    }

    /**
     * Starts receiving packets asynchronously from the current chat room.
     * Call {@link ChatApp#leaveRoom()} to stop receiving packets.
     */
    public void receivePacketsAsync() {
        new Thread(this::receivePackets, "PacketReceiver").start();
    }

    /**
     * Checks if the user is connected to a chat room.
     *
     * @return true if the user is connected, false otherwise
     */
    public boolean isConnected() {
        return multicastSocket != null && !multicastSocket.isClosed();
    }

}
