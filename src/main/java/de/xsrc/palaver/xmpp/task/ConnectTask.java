package de.xsrc.palaver.xmpp.task;

import de.xsrc.palaver.beans.Account;
import de.xsrc.palaver.xmpp.ConnectionSetupFactory;
import de.xsrc.palaver.xmpp.exception.ConnectionFailedException;
import javafx.collections.ObservableMap;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.util.XmppStringUtils;

import java.io.IOException;
import java.util.logging.Logger;

public class ConnectTask extends AbstractConnectionTask<XMPPTCPConnection> {

    private static final Logger logger = Logger.getLogger(ConnectTask.class
            .getName());
    private final Account account;
    private ObservableMap<Account, XMPPTCPConnection> connectionMap;

     public ConnectTask(Account account, ObservableMap<Account, XMPPTCPConnection> connectionMap) {
        super();
        this.account = account;
        this.connectionMap = connectionMap;
        this.updateTitle("Connecting to " + account.getJid());
    }


    @Override
    protected XMPPTCPConnection call() throws ConnectionFailedException {
        logger.finer("Connecting to account " + account);
        final String jid = account.getJid();
        final String serviceName = XmppStringUtils.parseDomain(jid);

        XMPPTCPConnectionConfiguration.Builder builder = getConfigurationBuilder(serviceName);

        XMPPTCPConnection connection = new XMPPTCPConnection(builder.build());
        try {
            connection.connect();
            logger.fine(String.format("Connection to serviceName %s " +
                    "successful", serviceName));
        } catch (SmackException | IOException | XMPPException e) {
            final String message = String.format("Connection to serviceName %s failed", serviceName);
            logger.severe(message);
            throw new ConnectionFailedException(message, e);
        }

        connection = ConnectionSetupFactory.setupConnection(connection, account);

        connectionMap.put(account, connection);
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