package de.xsrc.palaver.xmpp.task;

import de.xsrc.palaver.AbstractTest;
import de.xsrc.palaver.Connection;
import de.xsrc.palaver.beans.Credentials;
import de.xsrc.palaver.xmpp.exception.*;
import de.xsrc.palaver.xmpp.exception.AuthenticationException;
import de.xsrc.palaver.xmpp.exception.ConnectionException;
import javafx.embed.swing.JFXPanel;
import org.junit.*;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertFalse;

public class DeleteAccountTaskTest extends AbstractTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();
    private Credentials credentials;
    private Connection connection;

    @BeforeClass
    public static void initializeJFX() {
        new JFXPanel(); // Initialize JFX :)
    }

    @Before
    public void createAccount() throws ConnectionException, AccountCreationException {
        credentials = getMockCredentials();
        final CreateAccountTask createAccountTask = new CreateAccountTask(credentials, getObservableMap());
        connection = createAccountTask.call();
    }

    @After
    public void closeConnection() {
        if (connection.xmpptcpConnection != null && connection.xmpptcpConnection.isConnected())
            connection.close();
    }

    @Test
    public void testDeletion() throws AccountDeletionException, ConnectionException {
        new DeleteAccountTask(credentials).call();
        exception.expect(AuthenticationException.class);
        (new ConnectTask(credentials, getObservableMap())).call();
    }

    @Test
    public void connectionShouldBeDisconnected() throws AccountDeletionException, ConnectionException {
        new DeleteAccountTask(connection).call();
        assertFalse("The connection should be disconnected", connection.xmpptcpConnection.isConnected());

    }
}