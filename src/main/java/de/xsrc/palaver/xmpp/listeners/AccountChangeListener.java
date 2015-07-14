package de.xsrc.palaver.xmpp.listeners;

import de.xsrc.palaver.AccountManager;
import de.xsrc.palaver.beans.Credentials;
import de.xsrc.palaver.xmpp.task.ConnectTask;
import de.xsrc.palaver.xmpp.task.DisconnectTask;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableMap;
import org.datafx.concurrent.ObservableExecutor;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.util.List;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 28.06.15.
 */
public class AccountChangeListener implements ListChangeListener<Credentials> {

    private ObservableExecutor executor;
    private ObservableMap<Credentials, XMPPTCPConnection> connectionMap;
    private AccountManager accountManager;

    public AccountChangeListener(AccountManager accountManager) {

        this.accountManager = accountManager;
    }

    @Override
    public void onChanged(Change<? extends Credentials> c) {
        while (c.next()) {
            if (c.getAddedSize() > 0) {
                List<? extends Credentials> list = c.getAddedSubList();
                for (Credentials credentials : list) {
                    accountManager.connect(credentials);
                    credentials.credentialsChangedProperty().addChangeListener(evt -> {
                        accountManager.disconnect(credentials);
                        accountManager.connect(credentials);
                    });
                }
            } else if (c.wasRemoved()) {
                for (Credentials credentials : c.getRemoved()) {
                    accountManager.disconnect(credentials);
                }

            }
        }

    }

}
