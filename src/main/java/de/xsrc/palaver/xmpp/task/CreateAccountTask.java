package de.xsrc.palaver.xmpp.task;

import de.xsrc.palaver.Connection;
import de.xsrc.palaver.beans.Credentials;
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

    private Credentials credentials;
    private ObservableMap<Credentials, XMPPTCPConnection> connections;

    public CreateAccountTask(Credentials credentials, ObservableMap<Credentials, XMPPTCPConnection> connections) {
        this.credentials = credentials;
        this.connections = connections;
    }

    @Override
    protected XMPPTCPConnection call() throws AccountCreationException {
        try {
            XMPPTCPConnectionConfiguration.Builder builder = getConfigurationBuilder(XmppStringUtils.parseDomain(credentials.getJid()));
            final XMPPTCPConnectionConfiguration configuration = builder.build();
            final XMPPTCPConnection connection = new XMPPTCPConnection(configuration);
            connection.connect();
            final AccountManager accountManager = AccountManager.getInstance(connection);
            accountManager.createAccount(XmppStringUtils.parseLocalpart(credentials.getJid()), credentials.getPassword());
            XMPPTCPConnection c = (new Connection(credentials,connection)).xmpptcpConnection;
            connections.put(credentials, c);
            return c;
        } catch (Exception e) {
            throw new AccountCreationException("Account creation failed", e);
        }
    }
}
