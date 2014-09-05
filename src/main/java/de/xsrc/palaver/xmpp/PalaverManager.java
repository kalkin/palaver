package de.xsrc.palaver.xmpp;

import de.xsrc.palaver.beans.Palaver;
import de.xsrc.palaver.utils.Utils;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.muc.MultiUserChat;

import java.util.logging.Logger;

public class PalaverManager {

	private static final Logger logger = Logger
					.getLogger(PalaverManager.class.getName());

	public static void sendMsg(Palaver palaver, String body) throws SmackException.NotConnectedException, XMPPException {
		logger.finest(String.format("Sending msg from %s to %s: %s", palaver.getAccount(), palaver.getRecipient(), body));


		if (palaver.getConference()) {
			MultiUserChat muc = MucManager.getInstance().getMuc(palaver);
			muc.sendMessage(body);
		} else {
			Message message = new Message(palaver.getRecipient());
			message.setType(Message.Type.chat);
			message.setBody(body);
			XMPPConnection connection = ConnectionManager.getConnection(palaver.getAccount());
			connection.sendPacket(message);
		}

	}
}
