package de.xsrc.palaver.xmpp.task;

import de.xsrc.palaver.AbstractTest;
import de.xsrc.palaver.beans.Contact;
import de.xsrc.palaver.beans.Credentials;
import de.xsrc.palaver.models.ContactModel;
import de.xsrc.palaver.xmpp.exception.ConnectionFailedException;
import de.xsrc.palaver.xmpp.listeners.ContactSynchronisationListener;
import javafx.beans.property.ListProperty;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.jxmpp.util.XmppStringUtils;

import static org.junit.Assert.assertEquals;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 01.07.15.
 */
public class ContactModelTest extends AbstractTest {

    private ListProperty<Credentials> credentialsList;
    private Credentials julia;
    private Roster juliasRoster;
    private ContactModel contactModel;
    private XMPPTCPConnection connection;

    @Before
    public void setUp() throws Exception {
        new JFXPanel();
        credentialsList = createMockAccounts(3);
        final ObservableList<Credentials> accountsList = this.credentialsList.get();
        julia = accountsList.get(0);
        initializeJuliasRoster(accountsList);
        connection = (new ConnectTask(julia, getObservableMap())).call().xmpptcpConnection;
        juliasRoster = Roster.getInstanceFor(connection);

        contactModel = new ContactModel();
        ContactSynchronisationListener.setupRosterEntriesSynchronisation(julia, juliasRoster, contactModel);
    }

    /**
     * Adds mock friends to julias roster.
     *
     * @param accountsList Accounts to add to juliasRoster
     * @throws ConnectionFailedException
     * @throws SmackException.NotLoggedInException
     * @throws SmackException.NoResponseException
     * @throws XMPPException.XMPPErrorException
     * @throws SmackException.NotConnectedException
     */
    private void initializeJuliasRoster(ObservableList<Credentials> accountsList) throws ConnectionFailedException, SmackException.NotLoggedInException, SmackException.NoResponseException, XMPPException.XMPPErrorException, SmackException.NotConnectedException {
        final XMPPTCPConnection connection = (new ConnectTask(julia, getObservableMap())).call().xmpptcpConnection;
        final Roster roster = Roster.getInstanceFor(connection);

        for (int i = 1; i < accountsList.size(); i++) {
            String juliasFriend = accountsList.get(i).getJid();
            roster.createEntry(juliasFriend, XmppStringUtils.parseLocalpart(juliasFriend), null);
        }
        connection.disconnect();
    }

    /**
     * Test if the ContactModel adds all the initial RosterEntries, added on the server side
     */
    @Test
    public void initialRosterEntriesSynchronisation() {
        assertEquals(juliasRoster.getEntryCount(), contactModel.getData().size());
    }

    @Test
    public void addContactNotInRoster() throws SmackException.NotLoggedInException, XMPPException.XMPPErrorException, SmackException.NotConnectedException, SmackException.NoResponseException {
        String nurse = "nurse@example.com";
        contactModel.subscribe(julia, nurse);
        final ObservableList<Contact> data = contactModel.getData();
        for (Contact c : data) {
            System.out.println(c.getId());
        }
        assertEquals(juliasRoster.getEntryCount(), data.size());
    }


    @Test
    @Ignore
    public void addContactWhileOffline() throws SmackException.NotLoggedInException, XMPPException.XMPPErrorException, SmackException.NotConnectedException, SmackException.NoResponseException, ConnectionFailedException {
        connection.disconnect();
        String nurse = "nurse@example.com";
        contactModel.subscribe(julia, nurse);
        final ObservableList<Contact> data = contactModel.getData();
        final XMPPTCPConnection newConnection = (new ConnectTask(julia, getObservableMap())).call().xmpptcpConnection;
        final Roster roster = Roster.getInstanceFor(newConnection);
        ContactSynchronisationListener.setupRosterEntriesSynchronisation(julia, roster, contactModel);
        assertEquals(juliasRoster.getEntryCount(), data.size());
    }

    @After
    public void tearDown() {
        if (connection != null && connection.isConnected()) {
            connection.disconnect();
        }

        removeMockAccounts(credentialsList);
    }
}