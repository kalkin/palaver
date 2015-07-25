package de.xsrc.palaver.xmpp.listeners;

import de.xsrc.palaver.Connection;
import de.xsrc.palaver.beans.Conversation;
import de.xsrc.palaver.models.ConversationManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.carbons.CarbonManager;
import org.jivesoftware.smackx.carbons.packet.CarbonExtension;
import org.jxmpp.util.XmppStringUtils;

import java.util.logging.Logger;

public class MsgListener implements StanzaListener {
    private static final Logger logger = Logger.getLogger(MsgListener.class
            .getName());

    private final ConversationManager conversationManager;
    private Connection connection;


    public MsgListener(Connection connection, ConversationManager conversationManager) {
        this.connection = connection;
        this.conversationManager = conversationManager;
    }

    @Override
    public void processPacket(Stanza stanza) throws SmackException.NotConnectedException {
        Message message = (Message) stanza;
        logger.fine("Received message from " + message.getFrom() + " to " + message.getTo());
        final String myJid = connection.getCredentials().getJid();
        final String body = message.getBody();
        final String fromJid = XmppStringUtils.parseBareJid(message.getFrom());

        if (body == null || body.length() <= 0) {
            logger.warning(String.format("Strange Message %s", message.toString()));
            return;
        }

        logger.finest(message.toString());

        final boolean sendByMe = isSendByMe(myJid, message);
        final boolean carbonReceived = isCarbonReceived(message);
        if (sendByMe && !carbonReceived) {
            logger.finer("Message written by us locally" + message.toString());
            return;
        }

        final Conversation conversation = getConversation(message, sendByMe);
        // msg does not need to be send if it's carbon copy or send not by me
        final boolean sendState = (carbonReceived || !sendByMe);
        conversation.addMessage(fromJid, body, sendState);

    }

    private Conversation getConversation(Message message, boolean sendByMe) {
        final String accountJid;
        final String receiverJid;
        final String fromJid = XmppStringUtils.parseBareJid(message.getFrom());
        final String toJid = XmppStringUtils.parseBareJid(message.getTo());
        if (sendByMe) {
            accountJid = fromJid;
            receiverJid = toJid;
        } else {
            accountJid = toJid;
            receiverJid = fromJid;
        }

        return conversationManager.openConversation(accountJid, receiverJid, false);
    }


    private boolean isSendByMe(String myJid, Message message) {
        final String fromJid = XmppStringUtils.parseBareJid(message.getFrom());
        final String toJid = XmppStringUtils.parseBareJid(message.getTo());
        if (fromJid.equals(myJid)) {
            logger.finer("Message send by me " + message.toString());
            return true;
        }
        logger.finer("Message received " + message.toString());
        return false;
    }

    private boolean isCarbonReceived(Message message) {
        final boolean carbonEnabled = CarbonManager.getInstanceFor(connection.xmpptcpConnection).getCarbonsEnabled();
        if (!carbonEnabled)
            return false;

        CarbonExtension carbonExtension = CarbonExtension.from(message);
        if (carbonExtension != null && carbonExtension.getDirection() == CarbonExtension.Direction.received) {
            return true;
        }
        return false;
    }


}
