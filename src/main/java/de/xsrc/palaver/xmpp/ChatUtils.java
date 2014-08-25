package de.xsrc.palaver.xmpp;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.carbons.CarbonManager;

import de.xsrc.palaver.model.Account;
import de.xsrc.palaver.model.Entry;
import de.xsrc.palaver.model.Palaver;
import de.xsrc.palaver.xmpp.model.Buddy;

public class ChatUtils {
	private static final Logger logger = Logger.getLogger(ChatUtils.class
			.getName());

	private static ConcurrentHashMap<Palaver, Chat> chatMap;
	private static ConcurrentHashMap<String, XMPPConnection> conMap;

	private static synchronized ConcurrentHashMap<Palaver, Chat> getChatMap() {
		if (chatMap == null) {
			chatMap = new ConcurrentHashMap<Palaver, Chat>();
		}
		return chatMap;
	}

	private static synchronized ConcurrentHashMap<String, XMPPConnection> getConMap() {
		if (conMap == null) {
			conMap = new ConcurrentHashMap<String, XMPPConnection>();
		}
		return conMap;
	}

	public synchronized static XMPPConnection getConnection(Account account) {
		XMPPConnection connection = getConMap().get(account);
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

	private static XMPPConnection connectAccount(Account a, XMPPConnection c)
			throws SmackException, IOException, XMPPException {
		logger.finer("Connecting to account " + a);
		String jid = a.getJid();

		ConnectionConfiguration config = configureConnection(StringUtils
				.parseServer(jid));
		// config.setDebuggerEnabled(true);

		if (c == null) {
			c = new XMPPTCPConnection(config);

		}
		c.connect();
		if (c.isConnected()) {
			c.login(StringUtils.parseName(jid), a.getPassword());
			CarbonManager.getInstanceFor(c).enableCarbons();
		}
		ChatManager.getInstanceFor(c).addChatListener(new ChatListener(a));
		getConMap().put(a.getJid(), c);

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

	public synchronized static Chat getChat(Palaver palaver) {
		Chat chat = getChatMap().get(palaver);
		if (chat == null) {
			chat = createChat(palaver);
			getChatMap().put(palaver, chat);
		}
		return chat;
	}

	private static Chat createChat(Palaver palaver) {
		XMPPConnection connection = getConMap().get(palaver.getAccount());

		return ChatManager.getInstanceFor(connection).createChat(
				palaver.getRecipient(), new MsgListener(palaver.getAccount()));
	}

	public static void sendMsg(Palaver p, Entry e) {
		logger.finer("Sending msg " + e);
		try {
			getChat(p).sendMessage(e.getBody());
		} catch (NotConnectedException | XMPPException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	protected static SSLContext getContext() throws Exception {
		try {
			SSLContext sslContext = SSLContext.getInstance("SSL");
			sslContext.init(null, getTrustManager(),
					new java.security.SecureRandom());

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
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };

		return trustAllCerts;
	}

	public static ObservableList<Buddy> getBuddys() {
		Collection<XMPPConnection> values = getConMap().values();
		ObservableList<Buddy> result = FXCollections
				.observableList(new LinkedList<Buddy>());
		for (XMPPConnection con : values) {
			Collection<RosterEntry> allEntries = con.getRoster().getEntries();
			for (RosterEntry rosterEntry : allEntries) {
				String name = rosterEntry.getName();
				if (name != null && name.length() > 0) {
					Buddy b = new Buddy();
					b.setName(name);
					b.setJid(StringUtils.parseBareAddress(rosterEntry.getUser()));
					b.setAccount(StringUtils.parseBareAddress(con.getUser()));
					result.add(b);
				}
			}

		}
		return result;
	}
}
