package br.ldamd.packets;

import br.ldamd.Packet;

/**
 * The UserLeavePacket class represents a packet that indicates a user has left the chat room.
 */
public class UserLeavePacket extends Packet {

    public UserLeavePacket(String username) {
        super(username);
    }

    @Override
    public void onReceive() {
        System.out.println(this.getUsername() + " left the room.");
    }
}
