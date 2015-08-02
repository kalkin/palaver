package de.xsrc.palaver.xmpp;

import de.xsrc.palaver.AbstractTest;
import de.xsrc.palaver.Connection;
import de.xsrc.palaver.beans.Contact;
import de.xsrc.palaver.beans.Credentials;
import de.xsrc.palaver.models.ContactManager;
import de.xsrc.palaver.xmpp.exception.ConnectionException;
import de.xsrc.palaver.xmpp.exception.GeneralXmppException;
import javafx.beans.property.ListProperty;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.jxmpp.util.XmppStringUtils;

import static org.junit.Assert.assertEquals;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 01.07.15.
 */
public class RosterManagerTest extends AbstractTest {

    private ListProperty<Credentials> credentialsList;
    private Credentials julia;
    private Roster juliasRoster;
    private ContactManager contactManager;
    private Connection connection;
    private RosterManager rosterManager;

    @Before
    public void setUp() throws Exception {
        new JFXPanel();
        credentialsList = createMockAccounts(3);
        final ObservableList<Credentials> accountsList = this.credentialsList.get();
        julia = accountsList.get(0);
        addMockRosterEntries(accountsList);

        connection = new Connection(julia);
        connection.open();

        contactManager = new ContactManager();
        rosterManager = new RosterManager(contactManager, "./tmp/");
        rosterManager.registerConnection(connection);
        juliasRoster = rosterManager.rosterMap.values().stream().findFirst().get();

    }

    /**
     * Adds mock roster entries to julias roster.
     *
     * @param accountsList Accounts to add to juliasRoster
     * @throws ConnectionException
     * @throws SmackException.NotLoggedInException
     * @throws SmackException.NoResponseException
     * @throws XMPPException.XMPPErrorException
     * @throws SmackException.NotConnectedException
     */
    private void addMockRosterEntries(ObservableList<Credentials> accountsList) throws ConnectionException,
            SmackException.NotLoggedInException, XMPPException.XMPPErrorException, SmackException.NotConnectedException,
            SmackException.NoResponseException {
        final Connection c = new Connection(julia);
        c.open();
        final XMPPTCPConnection connection = c.xmpptcpConnection;
        final Roster roster = Roster.getInstanceFor(connection);

        for (int i = 1; i < accountsList.size(); i++) {
            String juliasFriend = accountsList.get(i).getJid();
            roster.createEntry(juliasFriend, XmppStringUtils.parseLocalpart(juliasFriend), null);
        }
        c.close();
    }

    /**
     * Test if the ContactManager adds all the initial RosterEntries, added on the server side
     */
    @Test
    public void initialRosterEntriesSynchronisation() {
        assertEquals(juliasRoster.getEntryCount(), contactManager.getData().size());
    }

    @Test
    public void addContactNotInRoster() throws ConnectionException, GeneralXmppException, InterruptedException {
        String nurse = "nurse@example.com";
        rosterManager.subscribe(julia, nurse);
        Thread.sleep(100);
        final ObservableList<Contact> data = contactManager.getData();
        assertEquals(data.size(), juliasRoster.getEntryCount());
    }

    @After
    public void tearDown() {
        connection.close();
        removeMockAccounts(credentialsList);
    }
}