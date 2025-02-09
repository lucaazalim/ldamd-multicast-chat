package br.ldamd.packets;

import br.ldamd.Packet;

public class UserJoinPacket extends Packet {

    public UserJoinPacket(String username) {
        super(username);
    }

    @Override
    public void onReceive() {
        System.out.println(this.getUsername() + " joined the room.");
    }
}
