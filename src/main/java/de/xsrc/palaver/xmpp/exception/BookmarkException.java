package de.xsrc.palaver.xmpp.exception;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 26.07.15.
 */
public class BookmarkException extends Exception {

    public BookmarkException(String message) {
        super(message);
    }

    public BookmarkException(Exception e) {
        super("Something went wrong", e);
    }
}
