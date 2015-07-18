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

    public DisconnectTask(Credentials credentials, ObservableMap<Credentials, Connection> connections) {
        this.connection = connections.get(credentials).xmpptcpConnection;
    }

    @Override
    protected Boolean call() throws Exception {
        if (connection == null || !connection.isConnected()) {
            logger.info("Already disconnected ");
            return true;
        }
        connection.disconnect();
        if (!connection.isConnected()) {
            logger.info("Successfully disconnected " + connection.getUser());
            return true;
        }
        return false;
    }
}
