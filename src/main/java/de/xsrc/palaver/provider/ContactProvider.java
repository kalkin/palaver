package de.xsrc.palaver.provider;

import de.xsrc.palaver.model.Account;
import de.xsrc.palaver.xmpp.model.Contact;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.Roster.SubscriptionMode;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.util.StringUtils;

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

	protected static Contact createContact(String account, String jid, String name) {
		Contact contact = new Contact();
		contact.setJid(jid);
		contact.setAccount(account);
		contact.setName(name);
		return contact;
	}

	public void initRoster(Account account, Roster roster) {
		roster.setSubscriptionMode(SubscriptionMode.accept_all);
		accountToRoster.put(account, roster);
		for (RosterEntry r : roster.getEntries()) {
			Contact c = new Contact();
			c.setAccount(account.getId());
			c.setJid(r.getUser());
			if (r.getName() != null && r.getName().length() > 0) {
				c.setName(r.getName());
			} else {
				c.setName(StringUtils.parseName(c.getJid()));
			}
			if (!contacts.contains(c)) {
				contacts.add(c);
			}
		}
	}

	public void addContact(Account account, String jid)
					throws NotLoggedInException, NoResponseException, XMPPErrorException,
					NotConnectedException {
		logger.fine("Adding " + jid + " to roster " + account);
		String name = StringUtils.parseName(jid);
		accountToRoster.get(account).createEntry(jid, name, null);
		Contact contact = createContact(account.getJid(), jid, name);
		contacts.add(contact);
	}

	public ObservableList<Contact> getData() {
		return contacts;
	}
}
