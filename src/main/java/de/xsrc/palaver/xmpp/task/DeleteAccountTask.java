package de.xsrc.palaver.xmpp.task;

import de.xsrc.palaver.Connection;
import de.xsrc.palaver.beans.Credentials;
import de.xsrc.palaver.xmpp.exception.AccountDeletionException;
import de.xsrc.palaver.xmpp.exception.ConnectionFailedException;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 28.06.15.
 */
class DeleteAccountTask extends org.datafx.concurrent.DataFxTask<Connection> {

    private final Connection connection;

    public DeleteAccountTask(Connection connection) {
        this.connection = connection;
    }

    public DeleteAccountTask(Credentials credentials) {
        connection = new Connection(credentials);

    }

    @Override
    protected Connection call() throws AccountDeletionException, ConnectionFailedException {
        connection.delete();
        return connection;
    }
}
