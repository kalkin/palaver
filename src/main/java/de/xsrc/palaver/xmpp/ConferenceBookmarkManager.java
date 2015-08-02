package de.xsrc.palaver.xmpp;

import de.xsrc.palaver.Connection;
import de.xsrc.palaver.beans.Contact;
import de.xsrc.palaver.beans.Credentials;
import de.xsrc.palaver.models.ContactManager;
import de.xsrc.palaver.xmpp.exception.BookmarkException;
import de.xsrc.palaver.xmpp.exception.ConnectionException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.bookmarks.BookmarkManager;
import org.jivesoftware.smackx.bookmarks.BookmarkedConference;
import org.jxmpp.util.XmppStringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Keeps the conference bookmarks in sync with {@link ContactManager}
 */
public class ConferenceBookmarkManager {

    private static final Logger logger = Logger.getLogger(ConferenceBookmarkManager.class.getName());

    private final HashMap<String, XMPPTCPConnection> connectionMap = new HashMap<>();
    private ContactManager contactManager;

    public ConferenceBookmarkManager(ContactManager contactManager) {
        this.contactManager = contactManager;
    }

    /**
     * Add conference bookmark to the server and to {@link ContactManager}
     *
     * @param contact  Conference to add
     * @throws ConnectionException is thrown when connection is not authenticated
     * @throws BookmarkException is thrown when something else gone wrong, probably server side
     */
    public void addBookmark(Contact contact) throws ConnectionException, BookmarkException {
        logger.fine("Add conference bookmark " + contact);
        if(!contact.isConference())
            throw new BookmarkException("Contact not a conference " + contact);
        final String accountJid = contact.getAccount();
        final XMPPTCPConnection xmpptcpConnection = connectionMap.get(accountJid);
        final BookmarkManager bookmarkManager = getBookmarkManager(xmpptcpConnection);

        final String conferenceName = contact.getName();
        final String conferenceJid = contact.getJid();
        final boolean autoJoin = true;
        final String nick = XmppStringUtils.parseLocalpart(accountJid);
        final String password = null;

        try {
            bookmarkManager.addBookmarkedConference(conferenceName, conferenceJid, autoJoin, nick, password);
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
            throw new BookmarkException(e);
        }
        contactManager.addContact(contact);
    }


    /**
     * Registers a {@link Connection} so the {@link BookmarkManager} starts synchronisation and is able to add and
     * remove bookmarked conferences for this {@link Connection#credentials}.
     * @param connection
     * @throws ConnectionException
     * @throws BookmarkException
     */
    public void registerConnection(Connection connection) throws ConnectionException, BookmarkException {
        logger.finer("Register connection " + connection.getCredentials().getJid());
        final XMPPTCPConnection xmpptcpConnection = connection.xmpptcpConnection;
        final Credentials credentials = connection.getCredentials();
        final String jid = credentials.getJid();
        syncBookmarks(jid, xmpptcpConnection);
        connectionMap.put(jid, xmpptcpConnection);
    }

    /**
     * Syncs the server side {@link BookmarkedConference} to {@link ContactManager}.
     *
     * @param jid
     * @param xmpptcpConnection
     * @throws ConnectionException is thrown when connection is not authenticated
     * @throws BookmarkException is thrown when something else gone wrong, probably server side
     */
    protected void syncBookmarks(String jid, XMPPTCPConnection xmpptcpConnection) throws ConnectionException, BookmarkException {
        logger.finer("Sync conference bookmarks for " + jid);
        final BookmarkManager bookmarkManager = getBookmarkManager(xmpptcpConnection);
        final List<BookmarkedConference> bookmarkedConferences;
        try {
            bookmarkedConferences = bookmarkManager.getBookmarkedConferences();
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
            throw new BookmarkException(e);
        }

        // Get list of conferences already stored in contactManager
        final Set<Contact> localConferences = contactManager.getData().parallelStream().filter(
                contact -> contact.getAccount().equals(jid) && contact.isConference()).collect(Collectors.toSet());

        // Get list of conferences stored on the server
        final Set<Contact> serverConferences = bookmarkedConferences.parallelStream().map(bookmarkedConference -> {
            return ContactManager.createContact(jid, bookmarkedConference.getJid(), bookmarkedConference.getName(), true);
        }).collect(Collectors.toSet());

        serverConferences.parallelStream().filter(contact -> !localConferences.contains(contact)).
                forEach(contact -> contactManager.addContact(contact));
    }

    /**
     * Helper method around {@link BookmarkManager#getBookmarkManager(XMPPConnection)} which wraps the exceptions
     *
     * @param xmpptcpConnection
     * @return BookmarkManager for the given connection
     * @throws ConnectionException is thrown when connection is not authenticated
     * @throws BookmarkException is thrown when something else gone wrong, probably server side
     */
    private BookmarkManager getBookmarkManager(XMPPTCPConnection xmpptcpConnection) throws ConnectionException, BookmarkException {
        BookmarkManager bookmarkManager;
        try {
            bookmarkManager = BookmarkManager.getBookmarkManager(xmpptcpConnection);
        } catch (SmackException e) {
            throw new ConnectionException("Connection " + xmpptcpConnection + " not authenticated", e);
        } catch (XMPPException e) {
            throw new BookmarkException(e);
        }
        return bookmarkManager;
    }

    /**
     * Delete conference bookmark on the server and remove from {@link ContactManager}
     *
     * @param contact The conference to delete
     * @throws ConnectionException is thrown when connection is not registered
     * @throws BookmarkException is thrown when something else gone wrong, probably server side
     */
    public void deleteBookmark(Contact contact) throws ConnectionException, BookmarkException {
        logger.fine("Delete conference bookmark " + contact);
        if(!contact.isConference())
            throw new BookmarkException("Contact not a conference " + contact);
        final String accountJid = contact.getAccount();
        final String conferenceJid = contact.getJid();
        final XMPPTCPConnection xmppConnection = connectionMap.get(accountJid);
        if (xmppConnection == null)
            throw new ConnectionException(accountJid + " is not connected");

        try {
            final BookmarkManager bookmarkManager = getBookmarkManager(xmppConnection);
            bookmarkManager.removeBookmarkedConference(conferenceJid);
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException |
                SmackException.NotConnectedException e) {
            throw new BookmarkException(e);
        }
        contactManager.removeContact(contact);
    }
}
