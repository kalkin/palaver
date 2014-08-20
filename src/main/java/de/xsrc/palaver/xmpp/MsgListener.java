package de.xsrc.palaver.xmpp;

import java.util.logging.Logger;

import javafx.application.Platform;

import org.datafx.crud.CrudException;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;

import de.xsrc.palaver.model.Entry;
import de.xsrc.palaver.model.Palaver;
import de.xsrc.palaver.utils.Utils;

public class MsgListener implements MessageListener {
	private static final Logger logger = Logger.getLogger(MessageListener.class
			.getName());

	private String accountJid;

	public MsgListener(String accountJid) {
		this.accountJid = accountJid;
	}

	public void processMessage(Chat chat, Message message) {
		logger.finest(message.toString());
		String id = accountJid + ":"
				+ StringUtils.parseBareAddress(chat.getParticipant());

		Entry e = new Entry(StringUtils.parseBareAddress(message.getFrom()),
				message.getBody());
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

}
