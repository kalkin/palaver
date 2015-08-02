package de.xsrc.palaver.xmpp;

import de.xsrc.palaver.ConnectionManager;
import de.xsrc.palaver.beans.Conversation;
import de.xsrc.palaver.beans.HistoryEntry;
import de.xsrc.palaver.models.ConversationManager;
import javafx.collections.ListChangeListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 24.07.15.
 */
public class Sender {

    private static final Logger logger = Logger.getLogger(Sender.class.getName());

    private ConnectionManager connectionManager;

    public Sender(ConnectionManager connectionManager, ConversationManager conversationManager) {
        this.connectionManager = connectionManager;

        conversationManager.getData().addListener((ListChangeListener<Conversation>) change -> {
            while (change.next()) if (change.wasAdded()) {
                // Subscribe on all non conference conversations histories
                final Stream<? extends Conversation> oneToOneChats = change.getAddedSubList().parallelStream().filter(conversation -> !conversation.isConference());
                oneToOneChats.forEach(conversation -> conversation.history.entryListProperty().addListener(
                        (ListChangeListener<HistoryEntry>) c -> {
                            while (c.next()) if (c.wasAdded()) {
                                // filter all messages which weren'<send></send>
                                final Stream<? extends HistoryEntry> notSendMsgs = c.getAddedSubList().stream().filter(historyEntry -> !historyEntry.getSendState());
                                notSendMsgs.forEach(historyEntry -> sendMessage(historyEntry, conversation.getRecipient()));
                            }
                        }));
            }
        });
    }

    private void sendMessage(HistoryEntry historyEntry, String recipient) {
        final Message message = new Message(recipient);
        message.setType(Message.Type.normal);
        message.setBody(historyEntry.getBody());
        final XMPPTCPConnection connection = connectionManager.getConnection(historyEntry.getFrom());
        try {
            connection.sendStanza(message);
            historyEntry.setSendState(true);
        } catch (SmackException.NotConnectedException e) {
            logger.severe("Could not send message " + message);
        }
    }
}
