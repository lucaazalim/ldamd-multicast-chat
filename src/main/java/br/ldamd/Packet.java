package br.ldamd;

import java.io.*;

/**
 * The Packet class represents a serializable packet that can be sent and received in the chat application.
 */
public abstract class Packet implements Serializable {

    private final String username;

    public Packet(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    /**
     * Serializes the packet to a byte array.
     *
     * @return the serialized byte array
     * @throws IOException if an I/O error occurs during serialization
     */
    public byte[] serialize() throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(this);
            return bos.toByteArray();
        }
    }

    /**
     * Deserializes a byte array to a Packet instance.
     *
     * @param bytes the byte array to deserialize
     * @return the deserialized Packet instance
     * @throws IOException if an I/O error occurs during deserialization
     * @throws ClassNotFoundException if the class of the deserialized object cannot be found
     */
    public static Packet deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes); ObjectInputStream ois = new ObjectInputStream(bis)) {
            return (Packet) ois.readObject();
        }
    }

    /**
     * Defines the action to be performed when the packet is received.
     * This method should be implemented by subclasses to define specific behavior.
     */
    public abstract void onReceive();
}