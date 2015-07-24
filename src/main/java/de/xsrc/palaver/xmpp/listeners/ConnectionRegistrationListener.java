package de.xsrc.palaver.xmpp.listeners;

import de.xsrc.palaver.Connection;
import de.xsrc.palaver.ConnectionListener;
import de.xsrc.palaver.models.ConversationManager;
import org.jivesoftware.smack.filter.MessageTypeFilter;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 24.07.15.
 */
public class ConnectionRegistrationListener implements ConnectionListener {

    private final ConversationManager conversationManager;

    public ConnectionRegistrationListener(ConversationManager conversationManager){

        this.conversationManager = conversationManager;
    }


    @Override
    public void onChanged(Change<? extends String, ? extends Connection> change) {
        if(change.wasAdded()){
            final Connection connection = change.getValueAdded();
            connection.xmpptcpConnection.addSyncStanzaListener(new MsgListener(connection, conversationManager),
                    MessageTypeFilter.NORMAL_OR_CHAT);
        }
    }
}
