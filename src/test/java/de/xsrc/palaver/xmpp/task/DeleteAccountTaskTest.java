package de.xsrc.palaver.xmpp.task;

import de.xsrc.palaver.beans.Credentials;
import de.xsrc.palaver.xmpp.exception.AccountCreationException;
import de.xsrc.palaver.xmpp.exception.AccountDeletionException;
import de.xsrc.palaver.xmpp.exception.AuthenticationFailedException;
import de.xsrc.palaver.xmpp.exception.ConnectionFailedException;
import javafx.embed.swing.JFXPanel;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.junit.*;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertFalse;

public class DeleteAccountTaskTest extends AbstractConnectionTest {

    static final Credentials CREDENTIALS = getAccount("alice.delete.test@xsrc.de", "password");
    @Rule
    public ExpectedException exception = ExpectedException.none();
    private XMPPTCPConnection connection;

    @BeforeClass
    public static void initializeJFX() {
        new JFXPanel(); // Initialize JFX :)
    }

    @Before
    public void createAccount() throws ConnectionFailedException {
        final CreateAccountTask createAccountTask = new CreateAccountTask(CREDENTIALS, getObservableMap());
        try {
            connection = createAccountTask.call();
        } catch (AccountCreationException e) {
            connection = (new ConnectTask(CREDENTIALS, getObservableMap())).call();
        }
    }

    @After
    public void closeConnection() {
        if (connection != null && connection.isConnected())
            connection.disconnect();
    }

    @Test
    public void testDeletion() throws AccountDeletionException, ConnectionFailedException {
        final DeleteAccountTask task = new DeleteAccountTask(connection);
        task.call();
        exception.expect(AuthenticationFailedException.class);
        (new ConnectTask(CREDENTIALS, getObservableMap())).call();
    }

    @Test
    public void connectionShouldBeDisconnected() throws AccountDeletionException, ConnectionFailedException {
        final DeleteAccountTask task = new DeleteAccountTask(connection);
        final XMPPTCPConnection connection1 = task.call();
        assertFalse("The connection should be disconnected", connection1.isConnected());

    }
}