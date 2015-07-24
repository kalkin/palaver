package de.xsrc.palaver.xmpp.task;

import de.xsrc.palaver.Connection;
import de.xsrc.palaver.beans.Credentials;
import de.xsrc.palaver.xmpp.exception.ConnectionFailedException;
import javafx.collections.ObservableMap;

public class ConnectTask extends org.datafx.concurrent.DataFxTask<Connection> {

    private final Credentials credentials;
    private final ObservableMap<String, Connection> connectionMap;

    public ConnectTask(Credentials credentials, ObservableMap<String, Connection> connectionMap) {
        super();
        this.credentials = credentials;
        this.connectionMap = connectionMap;
        this.updateTitle("Connecting to " + credentials.getJid());
    }


    @Override
    protected Connection call() throws ConnectionFailedException {
        Connection connection = new Connection(credentials);
        connection.open();
        connectionMap.put(credentials.getJid(), connection);
        return connection;

    }
}