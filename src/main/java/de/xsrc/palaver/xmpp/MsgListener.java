package de.xsrc.palaver.xmpp;

import de.xsrc.palaver.model.Entry;
import de.xsrc.palaver.model.Palaver;
import de.xsrc.palaver.provider.PalaverProvider;
import de.xsrc.palaver.utils.ColdStorage;
import org.datafx.controller.context.ApplicationContext;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.util.StringUtils;

import java.util.logging.Logger;

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
		PalaverProvider provider = ApplicationContext.getInstance()
				.getRegisteredObject(PalaverProvider.class);
		Palaver palaver = provider.getById(id);
		palaver.history.addEntry(e);
		palaver.setClosed(false);
		palaver.setUnread(true);
			// TODO Why does write back handler do not handle this?
		ColdStorage.save(Palaver.class, provider.getData());
	}

}
