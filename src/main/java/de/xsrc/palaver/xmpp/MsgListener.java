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
import org.jivesoftware.smackx.carbons.CarbonManager;
import org.jivesoftware.smackx.carbons.packet.CarbonExtension;

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

			if (body != null && body.length() >= 0) {
				Entry entry = new Entry();
				entry.setBody(message.getBody());

				String fromJid = StringUtils.parseBareAddress(message.getFrom());
				String toJid = StringUtils.parseBareAddress(message.getTo());

				if (fromJid == null || fromJid.equals(account.getJid())) {
					// Messages send by us
					entry.setFrom(account.getJid());
					saveEntry(account.getJid(), toJid, entry);
				} else if (toJid.equals(account.getJid())) {
					// Messages sent to us
					entry.setFrom(fromJid);
					saveEntry(account.getJid(), fromJid, entry);
					Notifications.notify(StringUtils.parseName(fromJid), body);
				} else {
					logger.severe("Server is sending garbage? " + message.toString());
				}
			} else if (CarbonManager.getCarbon(message) != null) {
				handleCarbon(message);
			} else {
				logger.warning(String.format("Strange Message %s", message.toString()));
			}

		}
	}

	private void saveEntry(String account, String recipient, Entry entry) {
		Palaver palaver = PalaverProvider.getById(account, recipient);
		if(palaver == null) {
			logger.fine(String.format("Creating new palaver %s -> %s", account, recipient));
			palaver = PalaverProvider.createPalaver(account, StringUtils.parseName(recipient));
		}
		palaver.history.addEntry(entry);
		if (!account.equals(entry.getFrom())) {
			palaver.setUnread(true);
		}
		palaver.setClosed(false);
		PalaverProvider.save();
	}

	private void handleCarbon(Message message) throws SmackException.NotConnectedException {
		CarbonExtension extension = CarbonManager.getCarbon(message);
		if (extension.getDirection() == CarbonExtension.Direction.sent) {
			logger.finer(String.format("Found Carbon Message %s", message.toString()));
			Packet packet = extension.getForwarded().getForwardedPacket();
			processPacket(packet);
		}
	}
}
