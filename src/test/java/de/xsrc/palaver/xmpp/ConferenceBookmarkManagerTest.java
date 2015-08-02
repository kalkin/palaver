package de.xsrc.palaver.xmpp;

import de.xsrc.palaver.AbstractTest;
import de.xsrc.palaver.Connection;
import de.xsrc.palaver.NameGenerator;
import de.xsrc.palaver.beans.Contact;
import de.xsrc.palaver.beans.Credentials;
import de.xsrc.palaver.models.ContactManager;
import de.xsrc.palaver.xmpp.exception.AccountDeletionException;
import de.xsrc.palaver.xmpp.exception.BookmarkException;
import de.xsrc.palaver.xmpp.exception.ConnectionException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.bookmarks.BookmarkManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.jxmpp.util.XmppStringUtils;

import static org.junit.Assert.assertEquals;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 25.07.15.
 */
public class ConferenceBookmarkManagerTest extends AbstractTest {

    public static final int AMOUNT_BOOKMARKED_CONFERENCES = 3;
    private Credentials credentials;
    private Connection connection;
    private ContactManager contactManager;
    private ConferenceBookmarkManager conferenceBookmarkManager;

    @Before
    public void setUp() throws XMPPException, ConnectionException, SmackException, BookmarkException {
        contactManager = new ContactManager();
        conferenceBookmarkManager = new ConferenceBookmarkManager(contactManager);
        credentials = createMockAccounts(1).get(0);
        createBookmarks(credentials, AMOUNT_BOOKMARKED_CONFERENCES);
        connection = new Connection(credentials);
        connection.open();
        conferenceBookmarkManager.registerConnection(connection);

    }

    /**
     * This tests if all bookmarked conferences are synced to {@link de.xsrc.palaver.models.ContactManager} properly.
     */
    @Test
    public void syncConferenceBookmarksFromServer() throws XMPPException, SmackException {
        final int result = contactManager.getData().size();
        assertEquals(AMOUNT_BOOKMARKED_CONFERENCES, result);
    }

    /**
     * This tests if all bookmarked conferences are synced to {@link de.xsrc.palaver.models.ContactManager} properly.
     */
    @Test
    public void addConferenceBookmark() throws ConnectionException, BookmarkException, XMPPException, SmackException {
        conferenceBookmarkManager.addBookmark(getMockConferenceContact());
        final BookmarkManager bookmarkManager = BookmarkManager.getBookmarkManager(connection.xmpptcpConnection);
//        Thread.sleep(1000);
        final int result = bookmarkManager.getBookmarkedConferences().size();
        assertEquals(AMOUNT_BOOKMARKED_CONFERENCES + 1, result);
    }

    @Test
    public void removeConferenceBookmark() throws ConnectionException, BookmarkException, XMPPException, SmackException {
        final Contact contact = contactManager.getData().stream().findFirst().get();
        conferenceBookmarkManager.deleteBookmark(contact);
        assertEquals(AMOUNT_BOOKMARKED_CONFERENCES - 1, contactManager.getData().size());
        final BookmarkManager bookmarkManager = BookmarkManager.getBookmarkManager(connection.xmpptcpConnection);
        assertEquals(AMOUNT_BOOKMARKED_CONFERENCES - 1, bookmarkManager.getBookmarkedConferences().size());
    }



    private void createBookmarks(Credentials credentials, int amount) throws ConnectionException, XMPPException, SmackException {
        final Connection connection = new Connection(credentials);
        connection.open();
        final XMPPTCPConnection xmpptcpConnection = connection.xmpptcpConnection;
        final BookmarkManager bookmarkManager = BookmarkManager.getBookmarkManager(xmpptcpConnection);
        final String nick = XmppStringUtils.parseLocalpart(credentials.getJid());
        for (int i = 0; i < amount; i++) {
            final String mockConferenceJid = getMockConferenceJid();
            final String name = XmppStringUtils.parseLocalpart(mockConferenceJid);
            bookmarkManager.addBookmarkedConference(name, mockConferenceJid, true, nick, null);
        }
        connection.close();
    }

    protected String getMockConferenceJid() {
        final NameGenerator gen = new NameGenerator();
        return gen.getName() + "_test@muc.xsrc.de";
    }

    protected Contact getMockConferenceContact(){
        final String jidToAdd = getMockConferenceJid();
        return ContactManager.createContact(credentials.getJid(), jidToAdd, null, true);
    }

    @After
    public void tearDown() throws ConnectionException, AccountDeletionException {
        connection.delete();
    }
}
