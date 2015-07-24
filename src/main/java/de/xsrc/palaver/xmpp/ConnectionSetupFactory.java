package de.xsrc.palaver.xmpp;

import de.xsrc.palaver.beans.Account;
import de.xsrc.palaver.xmpp.exception.AuthenticationFailedException;
import de.xsrc.palaver.xmpp.exception.ConnectionFailedException;
import de.xsrc.palaver.xmpp.task.ConnectTask;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.carbons.CarbonManager;
import org.jxmpp.util.XmppStringUtils;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 29.06.15.
 */
public class ConnectionSetupFactory {
    private static final Logger logger = Logger.getLogger(ConnectTask.class.getName());

    /**
     * Authenticates the user and configures the connection to use {@link org.jivesoftware.smack.sm.packet.StreamManagement}
     * and {@link org.jivesoftware.smackx.carbons.packet.CarbonExtension}
     *
     * @param connection
     * @param account
     * @return
     * @throws ConnectionFailedException
     */
    public static XMPPTCPConnection setupConnection(XMPPTCPConnection connection, Account account) throws ConnectionFailedException {
        final String jid = account.getJid();
        final String username = XmppStringUtils.parseLocalpart(jid);
        final String password = account.getPassword();
        final String serviceName = XmppStringUtils.parseDomain(jid);


        connection.setUseStreamManagement(true);

        try {
            connection.login(username, password);
            final String message = String.format("Authentication for account %s failed", account);
            logger.fine(message);

        } catch (XMPPException | IOException | SmackException e) {
            final String message = String.format("Authentication for account %s failed", account);
            logger.severe(message);
            throw new AuthenticationFailedException(message, e);
        }

        final CarbonManager carbonManager = CarbonManager.getInstanceFor(connection);
        try {
            carbonManager.setCarbonsEnabled(true);
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
