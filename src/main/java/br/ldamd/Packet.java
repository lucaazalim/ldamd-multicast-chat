package br.ldamd;

import java.io.*;

public abstract class Packet implements Serializable {

    private final String username;

    public Packet(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public byte[] serialize() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(this);
            return bos.toByteArray();
        }
    }

    public abstract void onReceive();

    public static Packet deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (Packet) ois.readObject();
        }
    }
}
