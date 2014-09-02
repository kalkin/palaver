package de.xsrc.palaver.provider;

import de.xsrc.palaver.beans.Account;
import de.xsrc.palaver.utils.Utils;
import de.xsrc.palaver.xmpp.ConnectionManager;
import de.xsrc.palaver.xmpp.model.Contact;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.bookmarks.BookmarkManager;
import org.jivesoftware.smackx.bookmarks.BookmarkedConference;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public class ContactProvider {
	private static final Logger logger = Logger.getLogger(ContactProvider.class
					.getName());
	private ObservableMap<Account, Roster> accountToRoster;
	private ObservableList<Contact> contacts;

	public ContactProvider() {
		accountToRoster = FXCollections
						.observableMap(new ConcurrentHashMap<Account, Roster>());
		contacts = FXCollections
						.observableList(new CopyOnWriteArrayList<Contact>());
	}

	protected static Contact createContact(String account, String jid, String name, Boolean conference) {
		Contact contact = new Contact();
		contact.setAccount(account);
		contact.setJid(jid);
		if (name != null && name.length() > 0) {
			contact.setName(name);
		} else {
			contact.setName(StringUtils.parseName(contact.getJid()));
		}
		if (conference) {
			contact.setConference(true);
		}
		return contact;
	}


	public void initRoster(Account account, Roster roster) throws XMPPException, SmackException {
		roster.setSubscriptionMode(SubscriptionMode.accept_all);
		accountToRoster.put(account, roster);
		for (RosterEntry r : roster.getEntries()) {
			Contact c = createContact(account.getJid(), r.getUser(), null, false);
			if (!contacts.contains(c)) {
				contacts.add(c);
			}
		}
		BookmarkManager bookmarkManager = Utils.getBookmarkManager(account.getJid());
		Collection<BookmarkedConference> bookmarkedConferences = bookmarkManager.getBookmarkedConferences();
		for (BookmarkedConference bookmarkedConference : bookmarkedConferences) {
			Contact c = createContact(account.getJid(), bookmarkedConference.getJid(), bookmarkedConference.getName(), true);
			contacts.add(c);
		}


	}

	public Contact addContact(Account account, String jid)
	throws SmackException, XMPPException {
		XMPPConnection connection = ConnectionManager.getConnection(account);
		String name = StringUtils.parseName(jid);
		Contact contact = createContact(account.getJid(), jid, name, false);
		if (Utils.isMuc(connection, jid)) {
			logger.info(String.format("Adding MUC Bookmark %s to %s", jid, account.getJid()));
			BookmarkManager bookmarkManager = Utils.getBookmarkManager(account.getJid());
			bookmarkManager.addBookmarkedConference(jid, jid, true, StringUtils.parseName(account.getJid()), null);
			contact.setConference(true);
		} else {
			logger.info(String.format("Adding %s to roster %s", jid, account));

			accountToRoster.get(account).createEntry(jid, name, null);
		}

		contacts.add(contact);
		return contact;
	}


	public Contact get(String accountJid, String contactJid) {
		for (Contact contact : contacts) {
			if (contact.getAccount().equals(accountJid) && contact.getJid().equals(contactJid)) {
				return contact;
			}
		}
		return null;
	}


	public ObservableList<Contact> getData() {
		return contacts;
	}
}
