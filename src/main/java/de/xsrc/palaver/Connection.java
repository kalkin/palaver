package de.xsrc.palaver;

import de.xsrc.palaver.beans.Credentials;
import de.xsrc.palaver.xmpp.exception.AuthenticationFailedException;
import de.xsrc.palaver.xmpp.task.ConnectTask;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.carbons.CarbonManager;
import org.jxmpp.util.XmppStringUtils;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 14.07.15.
 */
public class Connection {

    public XMPPTCPConnection xmpptcpConnection;
    private static final Logger logger = Logger.getLogger(Connection.class.getName());

    /**
     * Authenticates the user and configures the connection to use {@link org.jivesoftware.smack.sm.packet.StreamManagement}
     * and {@link org.jivesoftware.smackx.carbons.packet.CarbonExtension}
     * @param credentials
     * @param xmpptcpConnection
     * @throws AuthenticationFailedException
     */
    public Connection(Credentials credentials, XMPPTCPConnection xmpptcpConnection) throws AuthenticationFailedException {
        this.xmpptcpConnection = xmpptcpConnection;
        final String jid = credentials.getJid();
        final String username = XmppStringUtils.parseLocalpart(jid);
        final String password = credentials.getPassword();
        final String serviceName = XmppStringUtils.parseDomain(jid);


        xmpptcpConnection.setUseStreamManagement(true);

        try {
            xmpptcpConnection.login(username, password);
            final String message = String.format("Authentication for account %s successful", credentials);
            logger.fine(message);

        } catch (XMPPException | IOException | SmackException e) {
            final String message = String.format("Authentication for account %s failed", credentials);
            logger.severe(message);
            throw new AuthenticationFailedException(message, e);
        }

        final CarbonManager carbonManager = CarbonManager.getInstanceFor(xmpptcpConnection);
        try {
            carbonManager.setCarbonsEnabled(true);
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

}
