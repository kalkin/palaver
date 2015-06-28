package de.xsrc.palaver.xmpp.task;

import org.datafx.concurrent.DataFxTask;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.util.logging.Logger;

public class DisconnectTask extends DataFxTask {
    private static final Logger logger = Logger.getLogger(DisconnectTask.class
            .getName());
    private XMPPTCPConnection connection;

    public DisconnectTask(XMPPTCPConnection connection) {
        this.connection = connection;
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
