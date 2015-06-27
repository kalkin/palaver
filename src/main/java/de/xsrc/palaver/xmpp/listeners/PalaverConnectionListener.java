package de.xsrc.palaver.xmpp.listeners;

import de.xsrc.palaver.beans.Contact;
import de.xsrc.palaver.beans.Palaver;
import de.xsrc.palaver.models.ContactModel;
import de.xsrc.palaver.models.PalaverModel;
import de.xsrc.palaver.utils.Utils;
import de.xsrc.palaver.xmpp.MucManager;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.bookmarks.BookmarkManager;
import org.jivesoftware.smackx.bookmarks.BookmarkedConference;
import org.jivesoftware.smackx.carbons.CarbonManager;
import org.jxmpp.util.XmppStringUtils;

import java.util.logging.Logger;

public class PalaverConnectionListener implements ConnectionListener {

	private static final Logger logger = Logger.getLogger(PalaverConnectionListener.class.getName());
	private String accountJid;


	@Override
	public void connected(XMPPConnection connection) {
		logger.info("Connected account " + connection.getServiceName());

	}

    @Override
	public void authenticated(XMPPConnection connection, boolean b) {
		logger.fine(String.format("Authenticated to %s", connection.getUser()));
		accountJid = XmppStringUtils.parseBareJid(connection.getUser());
		try {
			CarbonManager.getInstanceFor(connection).enableCarbons();
		} catch (XMPPException | SmackException e) {
			logger.warning("Could not enable carbons on " + connection.getUser());
		}

		syncBookmarks(connection);
		MucManager.getInstance().reconnect(accountJid);
	}

	private void syncBookmarks(XMPPConnection connection) {

		logger.fine(String.format("Syncing Bookmarks for %s", connection.getUser()));
		BookmarkManager bm = null;

		try {
			bm = BookmarkManager.getBookmarkManager(connection);
			ContactModel model = ContactModel.getInstance();
			for (BookmarkedConference conference : bm.getBookmarkedConferences()) {
				logger.finer(String.format("Adding %s", conference.getJid()));
				Contact contact = Utils.createContact(XmppStringUtils.parseBareJid(connection.getUser()), conference.getJid(), conference.getName(), true);
				model.addContact(contact);

				if (conference.isAutoJoin()) {
					Palaver palaver = PalaverModel.getInstance().getById(contact.getAccount(), contact.getJid());
					if (palaver == null || palaver.getClosed()) {
						PalaverModel.getInstance().openPalaver(contact);
					}

				}

			}
		} catch (XMPPException | SmackException e) {
			e.printStackTrace();
			logger.warning("Syncing bookmarks for failed");
		}

	}

	@Override
	public void connectionClosed() {
		logger.info("Connection closed");
	}

	@Override
	public void connectionClosedOnError(Exception e) {
		logger.warning(String.format("Connection closed with error %s", e.getMessage()));
		MucManager.getInstance().disconnect(accountJid);
	}

	@Override
	public void reconnectingIn(int seconds) {
		logger.fine(String.format("Reconnecting in %d", seconds));
	}

	@Override
	public void reconnectionSuccessful() {
		logger.info("Reconnection successful");
	}

	@Override
	public void reconnectionFailed(Exception e) {
		logger.warning(String.format("Reconnection failed with error %s", e.getMessage()));
	}


}
