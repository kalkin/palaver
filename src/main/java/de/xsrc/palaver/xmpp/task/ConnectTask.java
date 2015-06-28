package de.xsrc.palaver.xmpp.task;

import de.xsrc.palaver.beans.Account;
import de.xsrc.palaver.xmpp.exception.AuthenticationFailedException;
import de.xsrc.palaver.xmpp.exception.ConnectionFailedException;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.carbons.CarbonManager;
import org.jxmpp.util.XmppStringUtils;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ConnectTask extends AbstractConnectionTask<XMPPTCPConnection> {

    private static final Logger logger = Logger.getLogger(ConnectTask.class
            .getName());
    private final Account account;
    private ConcurrentHashMap<String, XMPPTCPConnection> connectionMap;

    public ConnectTask(Account account) {
        this(account, new  ConcurrentHashMap<String, XMPPTCPConnection>());
    }

    public ConnectTask(Account account, ConcurrentHashMap<String, XMPPTCPConnection> connectionMap) {
        super();
        this.account = account;
        this.connectionMap = connectionMap;
        this.updateTitle("Connecting to " + account.getJid());
    }


    @Override
    protected XMPPTCPConnection call() throws ConnectionFailedException {
        logger.finer("Connecting to account " + account);
        final String jid = account.getJid();
        final String username = XmppStringUtils.parseLocalpart(jid);
        final String password = account.getPassword();
        final String serviceName = XmppStringUtils.parseDomain(jid);

        XMPPTCPConnectionConfiguration.Builder builder = getConfigurationBuilder(serviceName);
        builder.setUsernameAndPassword(username, password);

//		DirectoryRosterStore directoryRosterStore = Utils.getRosterStore(account);
//		config.setRosterStore(directoryRosterStore);
        // TODO: move this logic some where else

        XMPPTCPConnection connection = new XMPPTCPConnection(builder.build());
//		connection.addConnectionListener(new PalaverConnectionListener());
        connection.setUseStreamManagement(true);
        try {
            connection.connect();
        } catch (SmackException | IOException | XMPPException e) {
            final String message = String.format("Connection to account %s failed", account);
            logger.severe(message);
            throw new ConnectionFailedException(message, e);
        }

        try {
            connection.login();
            connectionMap.put(account.getJid(), connection);
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

//        Roster.getInstanceFor(connection).addRosterListener(new PalaverRosterListener(account));
        // TODO Fix connection
//		connection.addPacketListener(new MsgListener(account), MessageTypeFilter.acceptSpecific(Message.Type.chat));
//		connection.addPacketSendingListener(new MsgListener(account), new MessageTypeFilter(Message.Type.chat));

//		for (RosterPacket.Item item : directoryRosterStore.getEntries()) {
//			Contact contact = Utils.createContact(account.getJid(), item.getUser(), item.getName(), false
//			);
//			ContactModel.getInstance().addContact(contact);
//    }

    }
}