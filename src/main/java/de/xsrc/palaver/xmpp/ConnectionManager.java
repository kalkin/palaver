package de.xsrc.palaver.xmpp;

import de.xsrc.palaver.beans.Account;
import de.xsrc.palaver.listeners.PalaverRosterListener;
import de.xsrc.palaver.models.ContactModel;
import de.xsrc.palaver.provider.AccountProvider;
import de.xsrc.palaver.provider.ContactProvider;
import de.xsrc.palaver.utils.Utils;
import de.xsrc.palaver.xmpp.model.Contact;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.datafx.controller.context.ApplicationContext;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.RosterPacket;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.carbons.CarbonManager;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ConnectionManager {
	private static final Logger logger = Logger.getLogger(ConnectionManager.class
					.getName());
	private static ConcurrentHashMap<String, XMPPConnection> conMap;
	private static ConnectionManager instance;

	private ConnectionManager(ObservableList<Account> accounts) {
		accounts.addListener((ListChangeListener.Change<? extends Account> c) -> {
			ContactProvider provider = ApplicationContext.getInstance()
							.getRegisteredObject(ContactProvider.class);
			while (c.next()) {
				if (c.getAddedSize() > 0) {
					List<? extends Account> list = c.getAddedSubList();
					for (Account account : list) {
						logger.fine("Connecting to account " + account);
						ConnectionManager.getConnection(account);
					}
				}
			}

		});

	}

	public static void start(ObservableList<Account> accounts) {
		if (instance == null) {
			instance = new ConnectionManager(accounts);
			return;
		}
		throw new IllegalStateException("Connection Manager is already started");
	}

	public static ConnectionManager getInstance() {
		return instance;
	}

	private static synchronized ConcurrentHashMap<String, XMPPConnection> getConMap() {
		if (conMap == null) {
			conMap = new ConcurrentHashMap<>();
		}
		return conMap;
	}
	public static XMPPConnection getConnection(String accountJid) {
		Account account = AccountProvider.getByJid(accountJid);
		return ConnectionManager.getConnection(account);
	}
	public synchronized static XMPPConnection getConnection(Account account) {
		XMPPConnection connection = getConMap().get(account.getJid());
		if (connection == null) {
			try {
				connection = connectAccount(account);
			} catch (SmackException | IOException | XMPPException e) {
				logger.severe("Could not connect to " + account);
			}
		}
		return connection;
	}

	private static XMPPConnection connectAccount(Account account)
					throws SmackException, IOException, XMPPException {
		return connectAccount(account, null);
	}

	private static XMPPConnection connectAccount(Account account, XMPPConnection c)
					throws SmackException, IOException, XMPPException {
		logger.finer("Connecting to account " + account);
		String jid = account.getJid();

		ConnectionConfiguration config = configureConnection(StringUtils
						.parseServer(jid));
		config.setRosterLoadedAtLogin(true);
//		config.setDebuggerEnabled(true);


		DirectoryRosterStore directoryRosterStore = Utils.getRosterStore(account);
		config.setRosterStore(directoryRosterStore);
		// TODO: move this logic some where else
		for (RosterPacket.Item item : directoryRosterStore.getEntries()) {
			Contact contact = new Contact();
			contact.setConference(false);
			contact.setAccount(account.getJid());
			contact.setJid(item.getUser());
			if (item.getName() != null) {
				contact.setName(item.getName());
			} else {
				contact.setName(StringUtils.parseName(item.getUser()));
			}
			ContactModel.getInstance().addContact(contact);
		}


		if (c == null) {
			c = new XMPPTCPConnection(config);

		}

		c.connect();

		c.login(StringUtils.parseName(jid), account.getPassword(), "Palaver");
		CarbonManager.getInstanceFor(c).enableCarbons();
		c.getRoster().addRosterListener(new PalaverRosterListener(account));
		c.addPacketListener(new MsgListener(account), new MessageTypeFilter(Message.Type.chat));
		c.addPacketSendingListener(new MsgListener(account), new MessageTypeFilter(Message.Type.chat));
		getConMap().put(account.getJid(), c);
		return c;
	}

	private static ConnectionConfiguration configureConnection(String server) {
		ConnectionConfiguration config = new ConnectionConfiguration(server);
		SSLContext context;
		try {
			context = getContext();
			config.setCustomSSLContext(context);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return config;
	}

	protected static SSLContext getContext() throws Exception {
		try {
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext
							.init(null, getTrustManager(), new java.security.SecureRandom());

			return sslContext;
		} catch (Exception e) {
			throw new Exception("Error creating context for SSLSocket!", e);
		}
	}

	/**
	 * Create a trust manager that trusts all certificates It is not using a
	 * particular keyStore
	 */
	protected static TrustManager[] getTrustManager() {

		return new TrustManager[]{new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(
							java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(
							java.security.cert.X509Certificate[] certs, String authType) {
			}
		}};
	}

}
