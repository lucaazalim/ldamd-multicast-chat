package br.ldamd.packets;

import br.ldamd.Packet;

/**
 * The UserJoinPacket class represents a packet that indicates a user has joined the chat room.
 */
public class UserJoinPacket extends Packet {

    public UserJoinPacket(String username) {
        super(username);
    }

    @Override
    public void onReceive() {
        System.out.println(this.getUsername() + " joined the room.");
    }
}
