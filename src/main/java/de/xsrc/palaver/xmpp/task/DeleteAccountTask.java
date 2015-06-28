package de.xsrc.palaver.xmpp.task;

import de.xsrc.palaver.beans.Account;
import de.xsrc.palaver.xmpp.exception.AccountDeletionException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jxmpp.util.XmppStringUtils;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 28.06.15.
 */
public class DeleteAccountTask extends AbstractConnectionTask<XMPPTCPConnection> {

    private XMPPTCPConnection connection;

    public DeleteAccountTask(XMPPTCPConnection connection) {
        this.connection = connection;
    }

    public DeleteAccountTask(Account account) throws AccountDeletionException {
        final String jid = account.getJid();
        final String username = XmppStringUtils.parseLocalpart(jid);
        final String password = account.getPassword();
        final String serviceName = XmppStringUtils.parseDomain(jid);
        try {
            XMPPTCPConnectionConfiguration.Builder builder = getConfigurationBuilder(serviceName);
            builder.setUsernameAndPassword(username, password);
            this.connection = new XMPPTCPConnection(builder.build());
            this.connection.connect();
            this.connection.login();
        } catch (Exception e) {
            throw new AccountDeletionException("Account deletion failed", e);
        }

    }

    @Override
    protected XMPPTCPConnection call() throws AccountDeletionException {
        try {
            final AccountManager accountManager = AccountManager.getInstance(connection);
            accountManager.deleteAccount();
            connection.disconnect();
            return connection;
        } catch (Exception e) {
            throw new AccountDeletionException("Account deletion failed", e);
        }
    }
}
