package de.xsrc.palaver.xmpp.listeners;

import de.xsrc.palaver.beans.Account;
import de.xsrc.palaver.xmpp.task.ConnectTask;
import de.xsrc.palaver.xmpp.task.DisconnectTask;
import javafx.collections.ListChangeListener;
import org.datafx.concurrent.ObservableExecutor;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 28.06.15.
 */
public class AccountChangeListener implements ListChangeListener<Account> {

    private ObservableExecutor executor;
    private ConcurrentHashMap<String, XMPPTCPConnection> connectionMap;

    public AccountChangeListener(ObservableExecutor executor, ConcurrentHashMap connectionMap) {
        this.executor = executor;
        this.connectionMap = connectionMap;
    }

    @Override
    public void onChanged(Change<? extends Account> c) {
        while (c.next()) {
            if (c.getAddedSize() > 0) {
                List<? extends Account> list = c.getAddedSubList();
                for (Account account : list) {
                    executor.submit(getConnectTask(account, connectionMap));
                    account.credentialsChangedProperty().addChangeListener(evt -> {
                        executor.submit(new DisconnectTask(connectionMap.get(account.getJid())));
                        executor.submit(getConnectTask(account, connectionMap));
                    });
                }
            } else if (c.wasRemoved()) {
                for (Account account : c.getRemoved()) {
                    executor.submit(new DisconnectTask(connectionMap.get(account.getJid())));
                }

            }
        }

    }

    private ConnectTask getConnectTask(Account account, ConcurrentHashMap<String, XMPPTCPConnection> connectionMap) {
        return new ConnectTask(account, connectionMap);
    }
}
