package de.xsrc.palaver.xmpp.exception;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 28.06.15.
 */
public class ConnectionFailedException extends Exception {

    public ConnectionFailedException(String message, Exception cause) {
        super(message, cause);
    }

    public ConnectionFailedException(String message) {
        super(message);
    }
}
