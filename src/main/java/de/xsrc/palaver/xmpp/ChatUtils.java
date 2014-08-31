package de.xsrc.palaver.xmpp;

import de.xsrc.palaver.model.Account;
import de.xsrc.palaver.model.Entry;
import de.xsrc.palaver.model.Palaver;
import de.xsrc.palaver.xmpp.model.Contact;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.carbons.CarbonManager;
import org.jivesoftware.smackx.muc.MultiUserChat;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class ChatUtils {
	private static final Logger logger = Logger.getLogger(ChatUtils.class
			.getName());

	private static ConcurrentHashMap<Palaver, Chat> chatMap;
	private static ConcurrentHashMap<String, MultiUserChat> mucMap;
	private static ConcurrentHashMap<String, XMPPConnection> conMap;

	private static synchronized ConcurrentHashMap<Palaver, Chat> getChatMap() {
		if (chatMap == null) {
			chatMap = new ConcurrentHashMap<>();
		}
		return chatMap;
	}

	private static synchronized ConcurrentHashMap<String, XMPPConnection> getConMap() {
		if (conMap == null) {
			conMap = new ConcurrentHashMap<>();
		}
		return conMap;
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

	public static MultiUserChat getMuc(Palaver p) {
		MultiUserChat muc = getMucMap().get(p.getRecipient());
		if (muc == null || !muc.isJoined()) {
			XMPPConnection connection = getConMap().get(p.getAccount());
			muc = new MultiUserChat(connection, p.getRecipient());
			try {
				muc.createOrJoin(StringUtils.parseName(p.getAccount()));
				muc.addMessageListener(packet -> {
					if (packet instanceof Message) {
						Message msg = (Message) packet;
						if(msg.getBody().length() > 0) {
							Entry e = new Entry(msg.getFrom(), msg.getBody());
							p.history.addEntry(e);
							p.setUnread(true);
						}

					}

				});
			} catch (XMPPErrorException | SmackException e) {
				e.printStackTrace();
			}
			getMucMap().put(p.getRecipient(), muc);
		} else {
			logger.warning("Chat room " + p.getRecipient()
					+ " already exists in mucMap, this is weird");
		}

		return muc;
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
		String servername = StringUtils.parseServer(p.getRecipient());
		try {
			if (servername.startsWith("muc")) {
				MultiUserChat muc = getMuc(p);
				Message msg = muc.createMessage();
				msg.setBody(e.getBody());
				muc.sendMessage(msg);
			} else {
				getChat(p).sendMessage(e.getBody());
			}
		} catch (NotConnectedException | XMPPException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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

		return new TrustManager[] { new X509TrustManager() {
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
	}

	public static ObservableList<Contact> getContacts() {
		Collection<XMPPConnection> values = getConMap().values();
		ObservableList<Contact> result = FXCollections
				.observableList(new LinkedList<>());
		for (XMPPConnection con : values) {
			Collection<RosterEntry> allEntries = con.getRoster().getEntries();
			for (RosterEntry rosterEntry : allEntries) {
				String name = rosterEntry.getName();
				if (name != null && name.length() > 0) {
					Contact b = new Contact();
					b.setName(name);
					b.setJid(StringUtils.parseBareAddress(rosterEntry.getUser()));
					b.setAccount(StringUtils.parseBareAddress(con.getUser()));
					result.add(b);
				}
			}

		}
		return result;
	}

	protected synchronized static ConcurrentHashMap<String, MultiUserChat> getMucMap() {
		if (mucMap == null) {
			mucMap = new ConcurrentHashMap<>();
		}
		return mucMap;
	}

}
