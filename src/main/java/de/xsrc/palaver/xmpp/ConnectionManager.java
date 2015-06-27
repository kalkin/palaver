package de.xsrc.palaver.xmpp;

import de.xsrc.palaver.beans.Account;
import de.xsrc.palaver.provider.AccountProvider;
import de.xsrc.palaver.xmpp.task.ConnectTask;
import de.xsrc.palaver.xmpp.task.DisconnectTask;
import javafx.beans.property.ListProperty;
import javafx.collections.ListChangeListener;
import org.datafx.concurrent.ObservableExecutor;
import org.datafx.controller.context.ApplicationContext;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ConnectionManager {
	private static final Logger logger = Logger.getLogger(ConnectionManager.class
					.getName());
	private static ConcurrentHashMap<String, XMPPTCPConnection> conMap;

	private ConnectionManager() {
		ListProperty<Account> accounts = ApplicationContext.getInstance().getRegisteredObject(AccountProvider.class).getData();
		conMap = new ConcurrentHashMap<>();
		accounts.addListener((ListChangeListener.Change<? extends Account> c) -> {
			ObservableExecutor executor = ApplicationContext.getInstance().getRegisteredObject(ObservableExecutor.class);
			while (c.next()) {
				if (c.getAddedSize() > 0) {
					List<? extends Account> list = c.getAddedSubList();
					for (Account account : list) {
						executor.submit(getConnectTask(account));
						account.credentialsChangedProperty().addChangeListener(evt -> {
							executor.submit(new DisconnectTask(conMap.get(account.getJid())));
							executor.submit(getConnectTask(account));
						});
					}
				} else if (c.wasRemoved()) {
					for (Account account : c.getRemoved()) {
						executor.submit(new DisconnectTask(conMap.get(account.getJid())));
					}

				}
			}

		});

	}

	public static XMPPTCPConnection getConnection(String account) {
		return conMap.get(account);
	}

	public static ConnectionManager getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public static XMPPTCPConnection getConnection(Account account) {
		return conMap.get(account.getJid());
	}

	public static ConcurrentHashMap<String, XMPPTCPConnection> getConMap() {
		return conMap;
	}

	private ConnectTask getConnectTask(Account account) {
		return new ConnectTask(account);
	}

	private static final class InstanceHolder {
		static final ConnectionManager INSTANCE = new ConnectionManager();
	}
}
