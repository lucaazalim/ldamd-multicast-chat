package br.ldamd.packets;

import br.ldamd.Packet;

/**
 * The MessagePacket class represents a packet that contains a message sent by a user.
 */
public class MessagePacket extends Packet {

    private final String message;

    public MessagePacket(String username, String message) {
        super(username);
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public void onReceive() {
        System.out.println(this.getUsername() + ": " + message);
    }
}