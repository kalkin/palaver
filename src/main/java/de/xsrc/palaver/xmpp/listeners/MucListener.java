package de.xsrc.palaver.xmpp.listeners;


import de.xsrc.palaver.beans.Conversation;
import de.xsrc.palaver.beans.HistoryEntry;
import de.xsrc.palaver.utils.Notifications;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jxmpp.util.XmppStringUtils;

import java.util.logging.Logger;

public class MucListener implements MessageListener {

    private static final Logger logger = Logger.getLogger(MucListener.class.getName());
    private Conversation conversation;

    public MucListener(Conversation conversation) {
        this.conversation = conversation;
        logger.finer("Create MucListener for " + conversation);
    }


    @Override
    public void processMessage(Message message) {
        logger.finer("Received muc message for " + conversation);
        final String body = message.getBody();
        logger.finest(message.toString());
        final String chatUsername = XmppStringUtils.parseResource(message.getFrom());
        final String myUsername = XmppStringUtils.parseLocalpart(conversation.getAccount());

        if (chatUsername.equals(myUsername)) {
            // Our own message, this can be ignored
            return;
        }

        if (message.getType() == Message.Type.groupchat && body != null && body.length() >= 0) {
            HistoryEntry historyEntry = new HistoryEntry(chatUsername, body);
            conversation.setUnread(true);
            historyEntry.setSendState(true);
            Notifications.notify(XmppStringUtils.parseLocalpart(message.getFrom()), message.getBody());
            conversation.add(historyEntry);
        } else {
            logger.warning("Strange muc message" + message.toString());
        }

    }
}
