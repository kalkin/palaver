package de.xsrc.palaver.xmpp;

import de.xsrc.palaver.model.Account;
import de.xsrc.palaver.model.Entry;
import de.xsrc.palaver.model.Palaver;
import de.xsrc.palaver.provider.PalaverProvider;
import de.xsrc.palaver.utils.Notifications;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

import java.util.logging.Logger;

public class MsgListener implements PacketListener {
	private static final Logger logger = Logger.getLogger(MsgListener.class
					.getName());

	private Account account;

	public MsgListener(Account account) {
		this.account = account;
	}

	@Override
	public void processPacket(Packet packet) throws SmackException.NotConnectedException {
		if (packet instanceof Message) {
			Message message = (Message) packet;
			String body = message.getBody();
			logger.finest(message.toString());
			if (body != null && body.length() >= 0) { // TODO carbon messages should be catched here
				Entry entry = new Entry();
				entry.setBody(message.getBody());

				String fromJid = StringUtils.parseBareAddress(message.getFrom());
				String toJid = StringUtils.parseBareAddress(message.getTo());
				Palaver palaver;
				if (fromJid == null || fromJid.equals(account.getJid())) {
					palaver = PalaverProvider.getById(account.getJid(), toJid);
					entry.setFrom(account.getJid());
				} else if (toJid.equals(account.getJid())) {
					palaver = PalaverProvider.getById(account.getJid(), fromJid);
					entry.setFrom(fromJid);
					Notifications.notify(StringUtils.parseName(fromJid), body);
				} else {
					logger.severe("Server is sending garbage? " + message.toString());
					return;
				}

				palaver.history.addEntry(entry);
				palaver.setUnread(true);
				palaver.setClosed(false);
//				PalaverProvider.save();
			} else {
				logger.warning("Message does not contain a body" + message.toString());
			}

		}
	}
}
