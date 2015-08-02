package de.xsrc.palaver.xmpp.listeners;

import de.xsrc.palaver.ConnectionManager;
import de.xsrc.palaver.beans.Credentials;
import javafx.collections.ListChangeListener;

import java.util.List;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 28.06.15.
 */
public class AccountChangeListener implements ListChangeListener<Credentials> {

    private final ConnectionManager connectionManager;

    public AccountChangeListener(ConnectionManager connectionManager) {

        this.connectionManager = connectionManager;
    }

    @Override
    public void onChanged(Change<? extends Credentials> c) {
        while (c.next()) {
            if (c.getAddedSize() > 0) {
                List<? extends Credentials> list = c.getAddedSubList();
                for (Credentials credentials : list) {
                    connectionManager.connect(credentials);
                    credentials.credentialsChangedProperty().addChangeListener(evt -> {
                        connectionManager.disconnect(credentials);
                        connectionManager.connect(credentials);
                    });
                }
            } else if (c.wasRemoved()) {
                c.getRemoved().forEach(connectionManager::disconnect);

            }
        }

    }

}
