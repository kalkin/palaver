package de.xsrc.palaver.xmpp.exception;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 26.07.15.
 */
public class GeneralXmppException extends Exception {

    public GeneralXmppException(Exception cause) {
        super("Something went horrible wrong", cause);
    }
}
