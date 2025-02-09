package br.ldamd.exceptions;

public class AlreadyConnectedToARoomException extends RuntimeException {

    public AlreadyConnectedToARoomException() {
        super("You are already connected to a room.");
    }

}
