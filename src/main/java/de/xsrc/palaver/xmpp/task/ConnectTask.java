package de.xsrc.palaver.xmpp.task;

import de.xsrc.palaver.beans.Account;
import de.xsrc.palaver.beans.Contact;
import de.xsrc.palaver.models.ContactModel;
import de.xsrc.palaver.utils.Utils;
import de.xsrc.palaver.xmpp.ConnectionManager;
import de.xsrc.palaver.xmpp.listeners.MsgListener;
import de.xsrc.palaver.xmpp.listeners.PalaverConnectionListener;
import de.xsrc.palaver.xmpp.listeners.PalaverRosterListener;
import org.datafx.concurrent.DataFxTask;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.roster.packet.RosterPacket;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.rosterstore.DirectoryRosterStore;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.util.XmppStringUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.util.logging.Logger;

public class ConnectTask extends DataFxTask<XMPPConnection> {

	private static final Logger logger = Logger.getLogger(ConnectTask.class
					.getName());
	private final Account account;

	public ConnectTask(Account account) {
		super();
		this.account = account;
		this.updateTitle("Connecting to " + account.getJid());
	}

	private static XMPPTCPConnectionConfiguration configureConnection(String server) {
        final XMPPTCPConnectionConfiguration.Builder builder = XMPPTCPConnectionConfiguration.builder();
        SSLContext context;
		try {
			context = getContext();
            builder.setCustomSSLContext(context);
			builder.setCustomSSLContext(context);
//			builder.setRosterLoadedAtLogin(true);
//		config.setDebuggerEnabled(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return builder.build();
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

	@Override
	protected XMPPConnection call() {
		logger.finer("Connecting to account " + account);
		String jid = account.getJid();

		XMPPTCPConnectionConfiguration config = configureConnection(XmppStringUtils
						.parseDomain(jid));

		DirectoryRosterStore directoryRosterStore = Utils.getRosterStore(account);
//		config.setRosterStore(directoryRosterStore);
		// TODO: move this logic some where else

		XMPPTCPConnection connection = new XMPPTCPConnection(config);
		ConnectionManager.getConMap().put(account.getJid(), connection);
		connection.addConnectionListener(new PalaverConnectionListener());
		try {
			connection.connect();
			connection.login(XmppStringUtils.parseLocalpart(jid), account.getPassword(), "Palaver");
		} catch (SmackException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XMPPException e) {
			e.printStackTrace();
		}


        Roster.getInstanceFor(connection).addRosterListener(new PalaverRosterListener(account));
        // TODO Fix connection
//		connection.addPacketListener(new MsgListener(account), MessageTypeFilter.acceptSpecific(Message.Type.chat));
//		connection.addPacketSendingListener(new MsgListener(account), new MessageTypeFilter(Message.Type.chat));

		for (RosterPacket.Item item : directoryRosterStore.getEntries()) {
			Contact contact = Utils.createContact(account.getJid(), item.getUser(), item.getName(), false
			);
			ContactModel.getInstance().addContact(contact);
		}

		return connection;
	}
}