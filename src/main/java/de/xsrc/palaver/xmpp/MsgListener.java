package de.xsrc.palaver.xmpp;

import java.util.logging.Logger;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;

import de.xsrc.palaver.model.Entry;

public class MsgListener implements MessageListener {
	private static final Logger logger = Logger.getLogger(MessageListener.class
			.getName());

	private String accountJid;

	public MsgListener(String accountJid) {
		this.accountJid = accountJid;
	}

	public void processMessage(Chat chat, Message message) {
		logger.finest(message.toString());
		String body = message.getBody();
		if (body == null || body.length() == 0) {
			logger.finer("Empty message from " + message.getFrom());
			return;
		}
		String id = accountJid + ":"
				+ StringUtils.parseBareAddress(chat.getParticipant());

		Entry e = new Entry(StringUtils.parseBareAddress(message.getFrom()),
				message.getBody());
		// TODO Fix me
		// try {
		// Palaver p = Storage.getById(Palaver.class, id);
		// Platform.runLater(() -> {
		// p.history.addEntry(e);
		// Storage.getList(Palaver.class).add(p);
		// });
		//
		// } catch (IllegalArgumentException e1) {
		// e1.printStackTrace();
		// logger.warning("Received msg but no palaver" + message);
		// }
	}

}
