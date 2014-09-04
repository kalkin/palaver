package de.xsrc.palaver.models;

import de.xsrc.palaver.beans.Account;
import de.xsrc.palaver.beans.Contact;
import de.xsrc.palaver.utils.Utils;
import de.xsrc.palaver.xmpp.ConnectionManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.datafx.concurrent.ObservableExecutor;
import org.datafx.controller.context.ApplicationContext;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.bookmarks.BookmarkManager;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import static de.xsrc.palaver.utils.Utils.createContact;

// Crosspoint between view, smack and ContactProvider (file backend)
public class ContactModel {
	private static final Logger logger = Logger.getLogger(ContactModel.class
					.getName());

	protected static ObservableList<Contact> data = FXCollections.observableList(new CopyOnWriteArrayList<>());

	protected static ObservableMap<String, Contact> accountContactObservableMap = FXCollections.observableMap(new ConcurrentHashMap<>());

	private ContactModel() {
		accountContactObservableMap.addListener((MapChangeListener<String, Contact>) change -> {
			if (change.wasAdded()) {
				Platform.runLater(() -> data.add(change.getValueAdded()));
			} else if (change.wasRemoved()) {
				Platform.runLater(() ->data.remove(change.getValueRemoved()));
			}
		});

	}

	public static ContactModel getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public ObservableList<Contact> getData() {
		return FXCollections.unmodifiableObservableList(data);
	}


	public void addContact(Contact contact) {
		accountContactObservableMap.put(contact.getJid(), contact);
	}

	public void updateContact(Contact contact) {
		throw new NotImplementedException();
	}

	public void removeContact(Contact contact) {
		logger.info("Removing " + contact.getJid());
		accountContactObservableMap.remove(contact.getJid());
		ObservableExecutor executor = ApplicationContext.getInstance().getRegisteredObject(ObservableExecutor.class);
		XMPPConnection connection = ConnectionManager.getConnection(contact.getAccount());
		executor.submit(() -> {
			try {
				if (contact.isConference()) {
					BookmarkManager.getBookmarkManager(connection).removeBookmarkedConference(contact.getJid());
				} else {
					Roster roster = connection.getRoster();
					RosterEntry entry = roster.getEntry(contact.getJid());
					roster.removeEntry(entry);

				}
			} catch (XMPPException e) {
				e.printStackTrace();
			} catch (SmackException e) {
				e.printStackTrace();


			}
		});
	}

	public Contact addContact(Account account, String jid)
					throws SmackException, XMPPException {
		XMPPConnection connection = ConnectionManager.getConnection(account);
		String name = StringUtils.parseName(jid);
		Contact contact = createContact(account.getJid(), jid, name, false);
		if (Utils.isMuc(connection, jid)) {
			logger.info(String.format("Adding MUC Bookmark %s to %s", jid, account.getJid()));
//			BookmarkManager bookmarkManager = Utils.getBookmarkManager(account.getJid());
//			bookmarkManager.addBookmarkedConference(jid, jid, true, StringUtils.parseName(account.getJid()), null);
			contact.setConference(true);
		} else {
			logger.info(String.format("Adding %s to roster %s", jid, account));
			connection.getRoster().createEntry(contact.getJid(), contact.getName(), null);
		}

		accountContactObservableMap.put(contact.getJid(), contact);
		return contact;
	}

	private static final class InstanceHolder {
		static final ContactModel INSTANCE = new ContactModel();
	}

}
