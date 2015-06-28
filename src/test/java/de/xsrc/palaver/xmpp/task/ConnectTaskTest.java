package de.xsrc.palaver.xmpp.task;

import de.xsrc.palaver.beans.Account;
import de.xsrc.palaver.xmpp.exception.AccountCreationException;
import de.xsrc.palaver.xmpp.exception.AccountDeletionException;
import de.xsrc.palaver.xmpp.exception.ConnectionFailedException;
import javafx.embed.swing.JFXPanel;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.carbons.CarbonManager;
import org.junit.*;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertTrue;

public class ConnectTaskTest extends AbstractConnectionTest {


    static final Account account = getAccount("alice.connect.test@xsrc.de", "password");
    private ConcurrentHashMap<String, XMPPTCPConnection> connectionMap;
    private XMPPTCPConnection connection;

    @BeforeClass
    public static void createAccount() throws AccountCreationException {
        new JFXPanel(); // Initialize JFX :)
        final CreateAccountTask createAccountTask = new CreateAccountTask(account);
        createAccountTask.call().disconnect();

    }

    @AfterClass
    public static void deleteAccount() throws AccountDeletionException {
        DeleteAccountTask task = new DeleteAccountTask(account);
        task.call();
    }

    @Before
    public void connect() throws ConnectionFailedException {
        connectionMap = new ConcurrentHashMap<>();
        connection = (new ConnectTask(account, connectionMap)).call();
    }

    @After
    public void disconnect(){
        connection.disconnect();
    }

    @Test
    public void connectionEstablishedAndAuthenticated() throws Exception {
        assertTrue("Connection to server is established", connection.isConnected());
        assertTrue("Connection to server is authenticated", connection.isAuthenticated());


    }

    @Test
    public void connectionIsManaged() throws ConnectionFailedException {
        XMPPTCPConnection connection2 = connectionMap.get(account.getJid());
        assertTrue("ConnectionManager Map contains appropriate connection", connection == connection2);
    }

    @Test
    public void carbonsEnabled() throws ConnectionFailedException {
        final CarbonManager carbonManager = CarbonManager.getInstanceFor(connection);
        assertTrue("Carbons are enabled", carbonManager.getCarbonsEnabled());
    }

    @Test
    public void streamManagementEnabled() throws ConnectionFailedException {
        assertTrue("Stream Management should be enabled", connection.isSmEnabled());
    }

}