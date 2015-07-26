package de.xsrc.palaver.xmpp.task;

import de.xsrc.palaver.beans.Conversation;
import de.xsrc.palaver.xmpp.listeners.MucListener;
import org.datafx.concurrent.DataFxTask;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jxmpp.util.XmppStringUtils;

import java.util.logging.Logger;


public class JoinMucTask extends DataFxTask {

    private final static Logger logger = Logger.getLogger(JoinMucTask.class.getName());

    private Conversation conversation;
    private XMPPConnection connection;

    public JoinMucTask(Conversation conversation, XMPPConnection connection) {
        this.conversation = conversation;
        this.connection = connection;
        this.updateTitle("Joining Conference " + conversation.getRecipient());

    }


    @Override
    protected MultiUserChat call() {
        final String conferenceJid = conversation.getRecipient();
        final String accountJid = conversation.getAccount();
        final String nickname = XmppStringUtils.parseLocalpart(accountJid);

        final MultiUserChat muc = MultiUserChatManager.getInstanceFor(connection).getMultiUserChat(conferenceJid);

        try {
            muc.createOrJoin(nickname);
            muc.addMessageListener(new MucListener(conversation));
            logger.finer(String.format("Joined %s with account %s", conferenceJid, accountJid));
            return muc;
        } catch (XMPPException.XMPPErrorException | SmackException e) {
            e.printStackTrace();
            logger.severe(String.format("Failed joining %s with account %s", conferenceJid, accountJid));
            return null;
        }
    }
}
