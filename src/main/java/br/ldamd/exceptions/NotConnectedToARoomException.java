package br.ldamd.exceptions;

public class NotConnectedToARoomException extends RuntimeException{

    public NotConnectedToARoomException() {
        super("You are not connected to a room.");
    }

}
