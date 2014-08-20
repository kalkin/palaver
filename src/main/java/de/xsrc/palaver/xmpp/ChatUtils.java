package de.xsrc.palaver.xmpp;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.util.StringUtils;

import de.xsrc.palaver.model.Account;
import de.xsrc.palaver.model.Entry;
import de.xsrc.palaver.model.Palaver;

public class ChatUtils {
	private static final Logger logger = Logger.getLogger(ChatUtils.class
			.getName());

	private static ConcurrentHashMap<Palaver, Chat> chatMap;
	private static ConcurrentHashMap<String, XMPPConnection> conMap;

	public static synchronized ConcurrentHashMap<Palaver, Chat> getChatMap() {
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

	public static XMPPConnection connectAccount(Account a)
			throws SmackException, IOException, XMPPException {
		logger.finer("Connecting to account " + a);
		String jid = a.getJid();
		XMPPConnection c = new XMPPTCPConnection(StringUtils.parseServer(jid));
		c.connect();
		c.login(StringUtils.parseName(jid), a.getPassword());
		ChatManager.getInstanceFor(c).addChatListener(new ChatListener(a));
		getConMap().put(a.getJid(), c);

		return c;
	}

	private synchronized static Chat getChat(Palaver palaver) {
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
			e1.printStackTrace();
		}
	}
}
