package de.xsrc.palaver;

import de.xsrc.palaver.beans.Credentials;
import de.xsrc.palaver.xmpp.task.ConnectTask;
import de.xsrc.palaver.xmpp.task.DisconnectTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.datafx.concurrent.ObservableExecutor;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 14.07.15.
 */
public class AccountManager {

    private ObservableExecutor executor;
    private static final Logger logger = Logger.getLogger(AccountManager.class.getName());

    private final ObservableMap<Credentials, XMPPTCPConnection> connections = FXCollections.observableMap(new
            ConcurrentHashMap<Credentials, XMPPTCPConnection>());

    public AccountManager(ObservableExecutor executor) {
        this.executor = executor;
    }

    public void connect(Credentials credentials) {
        if (!connections.containsKey(credentials)) {
            executor.submit(new ConnectTask(credentials, connections));
        } else {
            logger.severe("Connection for credentials " + credentials.getJid() + " already exists");
        }

    }

    public void disconnect(Credentials credentials) {
        if (connections.containsKey(credentials)) {
            final XMPPTCPConnection xmpptcpConnection = connections.get(credentials);
            executor.submit(new DisconnectTask(xmpptcpConnection));
            connections.remove(credentials);
        } else {
            logger.severe("Connection for credentials " + credentials.getJid() + " does not exist");
        }
    }
}
