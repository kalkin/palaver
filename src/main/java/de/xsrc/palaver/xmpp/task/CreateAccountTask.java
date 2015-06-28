package de.xsrc.palaver.xmpp.task;

import de.xsrc.palaver.beans.Account;
import de.xsrc.palaver.xmpp.exception.AccountCreationException;
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

    public CreateAccountTask(Account account) {
        this.account = account;
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
            connection.login(XmppStringUtils.parseLocalpart(account.getJid()), account.getPassword());
            return connection;
        } catch (Exception e) {
            throw new AccountCreationException("Account creation failed", e);
        }
    }
}
