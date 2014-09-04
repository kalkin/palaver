package de.xsrc.palaver.models;

import de.xsrc.palaver.beans.Account;
import de.xsrc.palaver.beans.Contact;
import de.xsrc.palaver.utils.Utils;
import de.xsrc.palaver.xmpp.ConnectionManager;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;
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
				data.addAll(change.getValueAdded());
			} else if (change.wasRemoved()) {
				data.removeAll(change.getValueRemoved());
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
		accountContactObservableMap.put(contact.getId(), contact);
	}

	public void updateContact(Contact contact) {
		throw new NotImplementedException();
	}

	public void removeContact(Contact contact) {
		accountContactObservableMap.remove(contact.getId());
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

		accountContactObservableMap.put(contact.getId(), contact);
		return contact;
	}

	private static final class InstanceHolder {
		static final ContactModel INSTANCE = new ContactModel();
	}

}
