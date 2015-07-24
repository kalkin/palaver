package de.xsrc.palaver.xmpp.task;

import de.xsrc.palaver.NameGenerator;
import de.xsrc.palaver.beans.Account;
import de.xsrc.palaver.xmpp.exception.AccountCreationException;
import de.xsrc.palaver.xmpp.exception.AccountDeletionException;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.embed.swing.JFXPanel;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.junit.After;
import org.junit.Before;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 28.06.15.
 */
public class StartUpTaskTest {

    protected ListProperty<Account> accounts;
    protected ObservableMap<Account, XMPPTCPConnection> connections;

    @Before
    public void createMockAccounts() {
        new JFXPanel();
        accounts = new SimpleListProperty<>(FXCollections.observableArrayList());
        connections = FXCollections.observableHashMap();
        for (int i = 0; i < 3; i++) {
            final Account account = getAccount();
            accounts.add(account);
            CreateAccountTask createAccountTask = new CreateAccountTask(account, connections);
            try {
                createAccountTask.call().disconnect();
            } catch (AccountCreationException e) {
                e.printStackTrace();
            }
        }
    }

//    @Test
//    public void shouldSurviveDisconnect() {
//        for (XMPPTCPConnection connection: connections.values()) {
//            assertTrue("Connection should be connected", connection.isConnected());
//        }
//    }

    @After
    public void removeMockAccounts() {
        for (Account account : accounts) {
            try {
                final DeleteAccountTask deleteAccountTask = new DeleteAccountTask(account);
                deleteAccountTask.call();
            } catch (AccountDeletionException e) {
                e.printStackTrace();
            }
        }
    }

    protected Account getAccount() {
        final NameGenerator gen = new NameGenerator();
        final Account account = new Account();
        account.setJid(gen.getName() + "@xsrc.de");
        account.setPassword("password");
        return account;
    }

}