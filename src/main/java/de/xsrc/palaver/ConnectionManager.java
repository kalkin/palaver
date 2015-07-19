package de.xsrc.palaver;

import de.xsrc.palaver.beans.Credentials;
import de.xsrc.palaver.xmpp.task.ConnectTask;
import de.xsrc.palaver.xmpp.task.DisconnectTask;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.datafx.concurrent.ObservableExecutor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 14.07.15.
 */
public class ConnectionManager {

    private static final Logger logger = Logger.getLogger(ConnectionManager.class.getName());
    private final ObservableExecutor executor;
    private final ObservableMap<Credentials, Connection> connections = FXCollections.observableMap(new ConcurrentHashMap<>());

    public ConnectionManager(ObservableExecutor executor) {
        this.executor = executor;
    }

    public void addConnectionEstablishedListener(ConnectionListener connectionListener){
        connections.addListener(connectionListener);
    }

    public void connect(Credentials credentials) {
        if (!connections.containsKey(credentials)) {
            executor.submit(new ConnectTask(credentials, connections));
        } else {
            logger.severe("Connection for credentials " + credentials.getJid() + " already exists");
            throw new RuntimeException("Duplicate Connection");
        }

    }

    public void disconnect(Credentials credentials) {
        if (connections.containsKey(credentials)) {
            executor.submit(new DisconnectTask(credentials, connections));
        } else {
            logger.severe("Connection for credentials " + credentials.getJid() + " does not exist");
            throw new RuntimeException("Connection does not exist");
        }
    }
}
