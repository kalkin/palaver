package de.xsrc.palaver.xmpp.listeners;


import de.xsrc.palaver.beans.HistoryEntry;
import de.xsrc.palaver.beans.Conversation;
import de.xsrc.palaver.utils.Notifications;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jxmpp.util.XmppStringUtils;

import java.util.logging.Logger;

public class MucListener implements PacketListener {

    private static final Logger logger = Logger
            .getLogger(MucListener.class.getName());
    private Conversation conversation;

    public MucListener(Conversation conversation) {
        this.conversation = conversation;
        logger.fine("Create MucListener for " + conversation);
    }

    @Override
    public void processPacket(Stanza packet) throws SmackException.NotConnectedException {
        if (packet instanceof Message) {
            Message message = (Message) packet;
            logger.finer("Received muc message");
            String body = message.getBody();
            logger.finest(message.toString());

            if (message.getType() == Message.Type.groupchat && body != null && message.getBody().length() >= 0) {
                HistoryEntry historyEntry = new HistoryEntry(XmppStringUtils.parseResource(message.getFrom()), message.getBody());
                conversation.history.addEntry(historyEntry);
                if (!message.getFrom().equals(XmppStringUtils.parseLocalpart(conversation.getAccount())))
                    conversation.setUnread(true);
                Notifications.notify(XmppStringUtils.parseLocalpart(message.getFrom()), message.getBody());

            }
        }
    }
}
