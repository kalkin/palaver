package de.xsrc.palaver.xmpp.task;

import de.xsrc.palaver.beans.Account;
import de.xsrc.palaver.xmpp.exception.AccountCreationException;
import javafx.embed.swing.JFXPanel;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static junit.framework.Assert.assertTrue;

public class CreateAccountTaskTest extends AbstractConnectionTest {

    final Account account = getAccount("alice.create.test@xsrc.de", "password");
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    @After
    public void cleanUpAccounts() {
        new JFXPanel(); // Initialize JFX :)
        try {
            (new DeleteAccountTask(account)).call();
        } catch (Exception e) {
        }
    }

    @Test
    public void testAccountCreationSuccess() throws AccountCreationException {
        final CreateAccountTask createAccountTask = new CreateAccountTask(account);
        final XMPPTCPConnection connection = createAccountTask.call();
        assertTrue(connection.isConnected());
        assertTrue(connection.isAuthenticated());
        connection.disconnect();
    }

    /**
     * Expects AccountCreationException to be thrown
     */
    @Test(expected = AccountCreationException.class)
    public void testAccountCreationFailure() throws AccountCreationException {
        final CreateAccountTask createAccountTask = new CreateAccountTask(getAccount("alice.test@example.invalid", "password"));
        createAccountTask.call();
    }
}