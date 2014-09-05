package de.xsrc.palaver.xmpp.listeners;

import de.xsrc.palaver.beans.Contact;
import de.xsrc.palaver.beans.Palaver;
import de.xsrc.palaver.models.ContactModel;
import de.xsrc.palaver.models.PalaverModel;
import de.xsrc.palaver.provider.PalaverProvider;
import de.xsrc.palaver.utils.Utils;
import de.xsrc.palaver.xmpp.MucManager;
import de.xsrc.palaver.xmpp.task.JoinMucTask;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import org.datafx.concurrent.ObservableExecutor;
import org.datafx.controller.context.ApplicationContext;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.bookmarks.BookmarkManager;
import org.jivesoftware.smackx.bookmarks.BookmarkedConference;
import org.jivesoftware.smackx.carbons.CarbonManager;

import java.util.logging.Logger;

public class PalaverConnectionListener implements ConnectionListener {

	private static final Logger logger = Logger.getLogger(PalaverConnectionListener.class.getName());
	private String accountJid;


	@Override
	public void connected(XMPPConnection connection) {
		logger.info("Connected account " + connection.getServiceName());

	}

	@Override
	public void authenticated(XMPPConnection connection) {
		accountJid = StringUtils.parseBareAddress(connection.getUser());
		try {
			CarbonManager.getInstanceFor(connection).enableCarbons();
		} catch (XMPPException e) {
			e.printStackTrace();
		} catch (SmackException e) {
			e.printStackTrace();
		}
		logger.fine(String.format("Authenticated to %s", connection.getUser()));
		syncBookmarks(connection);
		MucManager.getInstance().reconnect(accountJid);
	}

	private void syncBookmarks(XMPPConnection connection) {

		logger.info(String.format("Syncing Bookmarks for %s", connection.getUser()));
		BookmarkManager bm = null;

		try {
			bm = BookmarkManager.getBookmarkManager(connection);
			ContactModel model = ContactModel.getInstance();
			for (BookmarkedConference conference : bm.getBookmarkedConferences()) {
				logger.finer(String.format("Adding %s", conference.getJid()));
				Contact contact = Utils.createContact(StringUtils.parseBareAddress(connection.getUser()), conference.getJid(), conference.getName(), true);
				model.addContact(contact);

				if (conference.isAutoJoin()) {
					Palaver palaver = PalaverModel.getInstance().getById(contact.getAccount(), contact.getJid());
					if (palaver == null || palaver.getClosed()) {
						palaver = PalaverModel.getInstance().openPalaver(contact);
					}
					ApplicationContext.getInstance().getRegisteredObject(ObservableExecutor.class).submit(new JoinMucTask(palaver));
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
