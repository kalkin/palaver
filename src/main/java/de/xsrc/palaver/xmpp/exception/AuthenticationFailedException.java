package de.xsrc.palaver.xmpp.exception;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 28.06.15.
 */
public class AuthenticationFailedException extends ConnectionFailedException {
    public AuthenticationFailedException(String message, Exception cause) {
        super(message,cause);
    }
}
