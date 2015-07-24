package de.xsrc.palaver;

import de.xsrc.palaver.beans.Credentials;
import de.xsrc.palaver.xmpp.exception.AccountCreationException;
import de.xsrc.palaver.xmpp.exception.AccountDeletionException;
import de.xsrc.palaver.xmpp.exception.AuthenticationFailedException;
import de.xsrc.palaver.xmpp.exception.ConnectionFailedException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.carbons.CarbonManager;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jxmpp.util.XmppStringUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Represents an active (or activatable) protocol session. Connections are created by {@link de.xsrc
 * .palaver.xmpp.task.CreateAccountTask} and {@link de.xsrc.palaver.xmpp.task.ConnectTask}
 * Created by Bahtiar `kalkin-` Gadimov on 14.07.15.
 */
public class Connection {

    private static final Logger logger = Logger.getLogger(Connection.class.getName());
    private final Credentials credentials;
    public XMPPTCPConnection xmpptcpConnection;

    /**
     * Authenticates the user and configures the connection to use {@link org.jivesoftware.smack.sm.packet.StreamManagement}
     * and {@link org.jivesoftware.smackx.carbons.packet.CarbonExtension}
     *
     * @param credentials User credentials
     */
    public Connection(Credentials credentials) {
        this.credentials = credentials;
    }

    private static XMPPTCPConnectionConfiguration.Builder getConfigurationBuilder(String serviceName) throws
            ConnectionFailedException {
        final XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
        SSLContext context;
        try {
            context = getContext();
            builder.setCustomSSLContext(context);
            builder.setServiceName(serviceName);
            builder.setResource("palaver");
            return builder;
        } catch (SSLException e) {
            throw new ConnectionFailedException(e.getMessage(), e);
        }


    }

    private static SSLContext getContext() throws SSLException {
        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext
                    .init(null, getTrustManager(), new java.security.SecureRandom());

            return sslContext;
        } catch (Exception e) {
            throw new SSLException("Error creating context for SSLSocket!", e);
        }
    }

    /**
     * Create a trust manager that trusts all certificates It is not using a
     * particular keyStore
     */
    private static TrustManager[] getTrustManager() {

        return new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {
            }
        }};
    }

    /**
     * Establishes a connection to an XMPP Server. This method does not authenticate the user.
     *
     * @param serviceName XMPP Server to open to
     * @return configured {@link XMPPTCPConnection}
     * @throws ConnectionFailedException
     */
    private XMPPTCPConnection getConnection(String serviceName) throws ConnectionFailedException {
        XMPPTCPConnectionConfiguration.Builder builder = getConfigurationBuilder(serviceName);
        final XMPPTCPConnectionConfiguration configuration = builder.build();
        XMPPTCPConnection xmppConnection = new XMPPTCPConnection(configuration);
        try {
            xmppConnection.connect();
            logger.fine(String.format("Connection to serviceName %s successful", serviceName));
        } catch (SmackException | IOException | XMPPException e) {
            final String message = String.format("Connection to serviceName %s failed", serviceName);
            logger.severe(message);
            throw new ConnectionFailedException(message, e);
        }
        return xmppConnection;
    }

    /**
     * Opens protocol session
     *
     * @throws ConnectionFailedException
     */
    public void open() throws ConnectionFailedException {
        final String serviceName = getServiceName();
        XMPPTCPConnection xmppConnection = getConnection(serviceName);
        establishConnection(this.credentials, xmppConnection);

    }

    private String getServiceName() {
        return XmppStringUtils.parseDomain(credentials.getJid());
    }

    /**
     * Registers an account on the remote server
     *
     * @throws ConnectionFailedException
     * @throws AccountCreationException
     */
    public void register() throws AccountCreationException, ConnectionFailedException {
        final String serviceName = getServiceName();
        XMPPTCPConnection xmppConnection = getConnection(serviceName);
        logger.finer("Registering account for " + credentials.getJid());
        final AccountManager accountManager = AccountManager.getInstance(xmppConnection);
        try {
            accountManager.createAccount(XmppStringUtils.parseLocalpart(credentials.getJid()), credentials.getPassword());
            establishConnection(this.credentials, xmppConnection);
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
            throw new AccountCreationException("Account creation failed", e);
        }
    }

    /**
     * Deletes an account on the remote server. If successful or connection was closed previously it will close the
     * disconnect from server
     *
     * @throws ConnectionFailedException
     * @throws AccountDeletionException
     */
    public void delete() throws ConnectionFailedException, AccountDeletionException {
        boolean wasClosed = false;
        logger.fine("Deleting account for " + credentials.getJid());
        if (xmpptcpConnection == null) {
            this.open();
            wasClosed = true;
        }
        final AccountManager accountManager = AccountManager.getInstance(xmpptcpConnection);
        try {
            accountManager.deleteAccount();
            this.close();
            logger.finer("Account deletion success");
            logger.finer("are we connected ? " + this.xmpptcpConnection.isConnected());
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
            if (wasClosed) {
                close();
            }
            throw new AccountDeletionException("Account deletion failed", e);
        }

    }

    /**
     * Enables Stream Management, authenticates the user and enables Carbons.
     *
     * @throws ConnectionFailedException
     */
    private void establishConnection(Credentials credentials, XMPPTCPConnection xmppConnection) throws
            ConnectionFailedException {
        enableSM(xmppConnection);
        login(credentials, xmppConnection);
        enableCarbons(xmppConnection);
        this.xmpptcpConnection = xmppConnection;
    }

    private void enableSM(XMPPTCPConnection xmppConnection) {
        if (xmppConnection.isSmAvailable()) {
            logger.info("Enabling Stream Management support " + credentials.getJid());
            xmppConnection.setUseStreamManagement(true);
            if (!xmppConnection.isSmEnabled()) {
                logger.severe("Could not enable Stream Management for " + credentials.getJid());
            }
        } else {
            logger.severe("No Stream Management support for " + credentials.getJid());
        }
    }

    private void enableCarbons(XMPPTCPConnection xmppConnection) {
        final CarbonManager carbonManager = CarbonManager.getInstanceFor(xmppConnection);
        try {
            carbonManager.setCarbonsEnabled(true);
            logger.finer("Enabled carbons for " + credentials.getJid());
        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
            logger.warning("Enabling carbons failed for " + credentials.getJid());
            e.printStackTrace();
        }
    }

    private void login(Credentials credentials, XMPPTCPConnection xmppConnection) throws AuthenticationFailedException {
        final String username = XmppStringUtils.parseLocalpart(credentials.getJid());
        final String password = credentials.getPassword();
        try {
            xmppConnection.login(username, password, StringUtils.randomString(5));
            final String message = String.format("Authentication for account %s successful", credentials);
            logger.fine(message);

        } catch (XMPPException | IOException | SmackException e) {
            final String message = String.format("Authentication for account %s failed", credentials);
            logger.severe(message);
            throw new AuthenticationFailedException(message, e);
        }
    }

    /**
     * Closes the protocol session
     */
    public void close() {
        if (xmpptcpConnection != null)
            this.xmpptcpConnection.disconnect();
    }

    public Credentials getCredentials() {
        return credentials;
    }
}
