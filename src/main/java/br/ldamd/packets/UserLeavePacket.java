package br.ldamd.packets;

import br.ldamd.Packet;

public class UserLeavePacket extends Packet {

    public UserLeavePacket(String username) {
        super(username);
    }

    @Override
    public void onReceive() {
        System.out.println(this.getUsername() + " left the room.");
    }
}
