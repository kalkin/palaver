package de.xsrc.palaver.provider;

import de.xsrc.palaver.model.Account;
import de.xsrc.palaver.xmpp.ConnectionManager;
import de.xsrc.palaver.xmpp.model.Contact;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.bookmarks.BookmarkManager;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;

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

	private static boolean isMuc(XMPPConnection connection, String jid) throws NotConnectedException, XMPPErrorException, NoResponseException {
		ServiceDiscoveryManager discoManager = ServiceDiscoveryManager.getInstanceFor(connection);
		DiscoverInfo info = discoManager.discoverInfo(StringUtils.parseServer(jid));
		return info.containsFeature("http://jabber.org/protocol/muc/");
	}

	public void initRoster(Account account, Roster roster) {
		roster.setSubscriptionMode(SubscriptionMode.accept_all);
		accountToRoster.put(account, roster);
		for (RosterEntry r : roster.getEntries()) {
			Contact c = createContact(account.getJid(), r.getUser(), null, false);
			if (!contacts.contains(c)) {
				contacts.add(c);
			}
		}
	}

	public void addContact(Account account, String jid)
					throws SmackException, XMPPException {
		XMPPConnection connection = ConnectionManager.getConnection(account);
		String name = StringUtils.parseName(jid);
		Contact contact = createContact(account.getJid(), jid, name, false);
		if (isMuc(connection, jid)) {
			logger.info(String.format("Joining MUC %s with account %s", jid, account.getJid()));
			BookmarkManager bookmarkManager = BookmarkManager.getBookmarkManager(connection);
			bookmarkManager.addBookmarkedConference(jid, jid, true, StringUtils.parseName(account.getJid()), null);
			contact.setConference(true);
		} else {
			logger.info(String.format("Adding %s to roster %s", jid, account));

			accountToRoster.get(account).createEntry(jid, name, null);
		}

		contacts.add(contact);
	}

	public ObservableList<Contact> getData() {
		return contacts;
	}
}
