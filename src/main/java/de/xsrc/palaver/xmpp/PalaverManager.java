package de.xsrc.palaver.xmpp;

import de.xsrc.palaver.beans.Credentials;
import de.xsrc.palaver.beans.Conversation;
import javafx.collections.ObservableMap;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.util.logging.Logger;

public class PalaverManager {

    private static final Logger logger = Logger
            .getLogger(PalaverManager.class.getName());

    public static void sendMsg(Conversation conversation, String body, ObservableMap<Credentials, XMPPTCPConnection> connections) {
        logger.finest(String.format("Sending msg from %s to %s: %s", conversation.getAccount(), conversation.getRecipient(), body));


        if (conversation.getConference()) {
            return;
//            TODO FIX MucMessage handling
//			MultiUserChat muc = MucManager.getInstance().getMuc(palaver);
//			try {
//				muc.sendMessage(body);
//			} catch (SmackException.NotConnectedException e) {
//				e.printStackTrace();
//				logger.severe(String.format("Could not send msg to %s", palaver.getRecipient()));
//			}
        } else {
//            Message message = new Message(conversation.getRecipient());
//            message.setType(Message.Type.chat);
//            message.setBody(body);
//            XMPPConnection connection;
//            connections.values().forEach(xmpptcpConnection -> xmpptcpConnection.get);
//            try {
//                connection.sendPacket(message);
//            } catch (SmackException.NotConnectedException e) {
//                logger.severe(String.format("Could not send msg to conference %s", conversation.getRecipient()));
//            }
        }

    }
}
