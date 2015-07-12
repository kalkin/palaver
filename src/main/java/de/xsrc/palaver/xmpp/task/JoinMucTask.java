package de.xsrc.palaver.xmpp.task;

import de.xsrc.palaver.beans.Conversation;
import org.datafx.concurrent.DataFxTask;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.muc.MultiUserChat;

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
// TODO Fix Muc Connection
//		MultiUserChat muc = new MultiUserChat(connection, palaver.getRecipient());
//		try {
//			muc.createOrJoin(XmppStringUtils.parseLocalpart(palaver.getAccount()));
//			muc.addMessageListener(new MucListener(palaver));
//			// TODO Implement Subject fetching and setting as Contact name
//			Utils.getJoinedMucs().put(palaver.getId(), muc);
//			logger.info(String.format("Joined %s with account %s", palaver.getRecipient(), palaver.getAccount()));
//			try {
//				BookmarkManager bookmarkManager = BookmarkManager.getBookmarkManager(connection);
//				for (BookmarkedConference bookmarkedConference : bookmarkManager.getBookmarkedConferences()) {
//					if (bookmarkedConference.getJid().equals(palaver.getRecipient()) && !bookmarkedConference.isAutoJoin()) {
//						bookmarkManager.addBookmarkedConference(XmppStringUtils.parseLocalpart(palaver.getRecipient()), palaver.getRecipient(), true, XmppStringUtils.parseLocalpart(palaver.getAccount()), null);
//					}
//				}
//
//			} catch (XMPPException e) {
//				//	e.printStackTrace();
//			}
//			return muc;
//		} catch (XMPPException.XMPPErrorException | SmackException e) {
//			e.printStackTrace();
//			logger.warning(String.format("Failed joining %s with account %s", palaver.getRecipient(), palaver.getAccount()));
//			return null;
//		}
        return null;
    }
}
