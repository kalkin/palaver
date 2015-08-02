package de.xsrc.palaver.xmpp.task;

import de.xsrc.palaver.AbstractTest;
import de.xsrc.palaver.Connection;
import de.xsrc.palaver.beans.Credentials;
import de.xsrc.palaver.xmpp.exception.AccountCreationException;
import de.xsrc.palaver.xmpp.exception.AccountDeletionException;
import de.xsrc.palaver.xmpp.exception.ConnectionException;
import javafx.embed.swing.JFXPanel;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;

public class CreateAccountTaskTest extends AbstractTest {

    private Credentials credentials;
    private Connection connection;

    @After
    public void cleanUpAccounts() throws ConnectionException, AccountDeletionException {
        connection.delete();
    }

    @Before
    public void createAccount() throws ConnectionException, AccountCreationException {
        new JFXPanel(); // Initialize JFX :)
        credentials = getMockCredentials();
        final CreateAccountTask createAccountTask = new CreateAccountTask(credentials, getObservableMap());
        connection = createAccountTask.call();
    }

    @Test
    public void testAccountCreationSuccess() throws AccountCreationException, ConnectionException {
        XMPPTCPConnection xmpptcpConnection = connection.xmpptcpConnection;
        assertTrue(xmpptcpConnection.isConnected());
        assertTrue(xmpptcpConnection.isAuthenticated());
    }

    /**
     * Expects AccountCreationException to be thrown
     */
    @Test(expected = AccountCreationException.class)
    public void testAccountCreationFailure() throws AccountCreationException, ConnectionException {
        final CreateAccountTask createAccountTask = new CreateAccountTask(credentials, getObservableMap());
        createAccountTask.call();
    }

}