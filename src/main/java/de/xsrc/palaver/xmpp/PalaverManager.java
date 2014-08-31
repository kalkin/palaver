package de.xsrc.palaver.xmpp;

import de.xsrc.palaver.model.Palaver;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;

import java.util.logging.Logger;

public class PalaverManager {

	private static final Logger logger = Logger
					.getLogger(PalaverManager.class.getName());

	public static void sendMsg(Palaver palaver, String body) throws SmackException.NotConnectedException {
		logger.finest(String.format("Sending msg from %s to %s: %s", palaver.getAccount(), palaver.getRecipient(), body));

		Message message = new Message(palaver.getRecipient());
		message.setType(Message.Type.chat);
		message.setBody(body);

		XMPPConnection connection = ConnectionManager.getConnection(palaver.accountInstance());
		connection.sendPacket(message);
	}
}
