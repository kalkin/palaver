package de.xsrc.palaver.xmpp.task;

import de.xsrc.palaver.Connection;
import de.xsrc.palaver.beans.Credentials;
import javafx.collections.ObservableMap;
import org.datafx.concurrent.DataFxTask;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.util.logging.Logger;

public class DisconnectTask extends DataFxTask {
    private static final Logger logger = Logger.getLogger(DisconnectTask.class
            .getName());
    private final XMPPTCPConnection connection;
    private final Credentials credentials;
    private final ObservableMap<String, Connection> connections;

    public DisconnectTask(Credentials credentials, ObservableMap<String, Connection> connections) {
        this.credentials = credentials;
        this.connections = connections;
        this.connection = connections.get(credentials.getJid()).xmpptcpConnection;
    }

    @Override
    protected Boolean call() throws Exception {
        if (connection == null || !connection.isConnected()) {
            logger.info("Already disconnected ");
        }
        connection.disconnect();
        if (!connection.isConnected()) {
            logger.info("Successfully disconnected " + connection.getUser());
        }
        connections.remove(credentials.getJid());
        return connection.isConnected();
    }
}
