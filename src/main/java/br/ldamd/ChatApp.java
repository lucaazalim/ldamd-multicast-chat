package br.ldamd;

import br.ldamd.packets.MessagePacket;
import br.ldamd.packets.UserJoinPacket;
import br.ldamd.packets.UserLeavePacket;

import java.io.IOException;
import java.net.*;

public class ChatApp {

    private static final String ADDRESS_PREFIX = "230.0.0.";
    private static final int PORT = 6789;
    private static final int BUFFER_SIZE = 1000;

    private final String username;

    private InetAddress groupAddress;
    private InetSocketAddress group;
    private MulticastSocket multicastSocket;

    public ChatApp(String name) {
        this.username = name;
    }

    public void enterRoom(int roomNumber) {

        if (this.isConnected()) {
            throw new RuntimeException("You need to leave the room before joining another one.");
        }

        try {
            this.groupAddress = InetAddress.getByName(ADDRESS_PREFIX + roomNumber);
        } catch (UnknownHostException e) {
            throw new RuntimeException("Unknown host for the specified room number.", e);
        }

        this.group = new InetSocketAddress(this.groupAddress, PORT);

        try {

            this.multicastSocket = new MulticastSocket(PORT);
            this.multicastSocket.joinGroup(this.group, null);

            var handshakePacket = new UserJoinPacket(this.username);
            this.sendPacket(handshakePacket);

        } catch (IOException e) {
            throw new RuntimeException("Unable to join the specified room.", e);
        }

    }

    public void leaveRoom() {

        if (this.multicastSocket == null) {
            throw new RuntimeException("Already not in a room.");
        }

        var handshakePacket = new UserLeavePacket(this.username);
        this.sendPacket(handshakePacket);

        try {
            this.multicastSocket.leaveGroup(this.group, null);
        } catch (IOException e) {
            throw new RuntimeException("Unable to leave room.", e);
        }

        this.multicastSocket.close();
        this.groupAddress = null;
        this.group = null;

    }

    private void sendPacket(Packet packet) {

        if (!this.isConnected()) {
            throw new RuntimeException("Can't send packet before joining a room.");
        }

        byte[] serializedPacket;

        try {
            serializedPacket = packet.serialize();
        } catch (IOException e) {
            throw new RuntimeException("Unable to serialize the packet.", e);
        }

        var datagramPacketOut = new DatagramPacket(serializedPacket, serializedPacket.length, groupAddress, PORT);

        try {
            this.multicastSocket.send(datagramPacketOut);
        } catch (IOException e) {
            throw new RuntimeException("Unable to send packet.", e);
        }

    }

    public void sendMessage(String messageContent) {

        var message = new MessagePacket(this.username, messageContent);
        this.sendPacket(message);

    }

    public void receivePackets() {

        byte[] buffer = new byte[BUFFER_SIZE];

        while (this.isConnected()) {

            var datagramPacketIn = new DatagramPacket(buffer, BUFFER_SIZE);

            try {
                this.multicastSocket.receive(datagramPacketIn);
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

            buffer = new byte[BUFFER_SIZE];

        }

    }

    public void receivePacketsAsync() {
        new Thread(this::receivePackets).start();
    }

    public boolean isConnected() {
        return this.multicastSocket != null && !this.multicastSocket.isClosed();
    }

}
