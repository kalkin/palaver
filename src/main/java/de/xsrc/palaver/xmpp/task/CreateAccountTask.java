package de.xsrc.palaver.xmpp.task;

import de.xsrc.palaver.Connection;
import de.xsrc.palaver.beans.Credentials;
import de.xsrc.palaver.xmpp.exception.AccountCreationException;
import de.xsrc.palaver.xmpp.exception.ConnectionFailedException;
import javafx.collections.ObservableMap;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 27.06.15.
 */
class CreateAccountTask extends org.datafx.concurrent.DataFxTask<Connection> {

    private final Credentials credentials;
    private final ObservableMap<String, Connection> connectionMap;

    public CreateAccountTask(Credentials credentials, ObservableMap<String, Connection> connectionMap) {
        this.credentials = credentials;
        this.connectionMap = connectionMap;
    }

    @Override
    protected Connection call() throws AccountCreationException, ConnectionFailedException {
        Connection connection = new Connection(credentials);
        connection.register();
        connectionMap.put(credentials.getJid(), connection);
        return connection;
    }
}
