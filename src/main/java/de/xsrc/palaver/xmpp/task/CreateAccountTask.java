package de.xsrc.palaver.xmpp.task;

import de.xsrc.palaver.beans.Account;
import de.xsrc.palaver.xmpp.ConnectionSetupFactory;
import de.xsrc.palaver.xmpp.exception.AccountCreationException;
import javafx.collections.ObservableMap;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jxmpp.util.XmppStringUtils;

import java.util.logging.Logger;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 27.06.15.
 */
public class CreateAccountTask extends AbstractConnectionTask<XMPPTCPConnection> {

    private static final Logger logger = Logger.getLogger(ConnectTask.class
            .getName());

    private Account account;
    private ObservableMap<Account, XMPPTCPConnection> connections;

    public CreateAccountTask(Account account, ObservableMap<Account, XMPPTCPConnection> connections) {
        this.account = account;
        this.connections = connections;
    }

    @Override
    protected XMPPTCPConnection call() throws AccountCreationException {
        try {
            XMPPTCPConnectionConfiguration.Builder builder = getConfigurationBuilder(XmppStringUtils.parseDomain(account.getJid()));
            final XMPPTCPConnectionConfiguration configuration = builder.build();
            final XMPPTCPConnection connection = new XMPPTCPConnection(configuration);
            connection.connect();
            final AccountManager accountManager = AccountManager.getInstance(connection);
            accountManager.createAccount(XmppStringUtils.parseLocalpart(account.getJid()), account.getPassword());
            XMPPTCPConnection c = ConnectionSetupFactory.setupConnection(connection, account);
            connections.put(account, c);
            return c;
        } catch (Exception e) {
            throw new AccountCreationException("Account creation failed", e);
        }
    }
}
