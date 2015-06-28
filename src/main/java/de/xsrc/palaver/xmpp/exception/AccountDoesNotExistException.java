package de.xsrc.palaver.xmpp.exception;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 28.06.15.
 */
public class AccountDoesNotExistException extends Exception{
    public AccountDoesNotExistException(String message, Exception cause) {
        super(message,cause);
    }
}
