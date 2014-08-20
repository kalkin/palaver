package de.xsrc.palaver.utils;

import java.io.IOException;
import java.util.logging.Logger;

import javafx.application.Platform;

import org.datafx.crud.CrudException;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.util.StringUtils;

import de.xsrc.palaver.model.Account;
import de.xsrc.palaver.model.Entry;
import de.xsrc.palaver.model.Palaver;

public class ChatUtils {
	private static final Logger logger = Logger.getLogger(ChatUtils.class
			.getName());

	public static XMPPConnection connectAccount(Account a)
			throws SmackException, IOException, XMPPException {
		String jid = a.getJid();
		XMPPConnection c = new XMPPTCPConnection(StringUtils.parseServer(jid));
		c.connect();
		c.login(StringUtils.parseName(jid), a.getPassword());
		registerConnectionListers(c, a);
		return c;
	}

	public static XMPPConnection registerConnectionListers(XMPPConnection c,
			Account a) {
		ChatManager.getInstanceFor(c).addChatListener(
				(Chat chat, boolean createdLocaly) -> {
					if (!createdLocaly) {
						// Listen for new incoming chats
						String id = getPalaverId(chat, a);
						try {
							// if a new chat this should fail
							Palaver palaver = Utils.getStorage(Palaver.class)
									.getById(id);
							logger.finest("Retrieved palaver: " + palaver);
						} catch (Exception e) {
							String recipent = StringUtils.parseBareAddress(chat.getParticipant());
							Palaver p = new Palaver();
							p.setAccount(a.getJid());
							p.setRecipient(recipent);
							logger.finer("Created new palaver" + p);
							Platform.runLater(() -> {
								Utils.getStorage(Palaver.class).save(p);
							});
						}
						chat = registerChatListeners(chat, a);
					}
				});
		return c;
	}

	public static Chat registerChatListeners(Chat chat, Account a) {
		chat.addMessageListener(new MessageListener() {

			@Override
			public void processMessage(Chat chat, Message message) {
				logger.finest(message.toString());
				String id = getPalaverId(chat, a);

				Entry e = new Entry(StringUtils.parseBareAddress(message
						.getFrom()), message.getBody());
				try {
					Palaver p = Utils.getStorage(Palaver.class).getById(id);
					Platform.runLater(() -> {
						p.history.addEntry(e);
						Utils.getStorage(Palaver.class).save(p);
					});

				} catch (CrudException e1) {
					e1.printStackTrace();
					logger.warning("Received msg but no palaver" + message);
				}
			}
		});
		return chat;
	}

	public static String getPalaverId(Chat chat, Account a) {
		return a.getId() + ":"
				+ StringUtils.parseBareAddress(chat.getParticipant());
	}
}
