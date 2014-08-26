package de.xsrc.palaver.provider;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.util.StringUtils;

import de.xsrc.palaver.model.Account;
import de.xsrc.palaver.xmpp.model.Contact;

public class ContactProvider {
	private ObservableMap<Account, Roster> accountToRoster;
	private ObservableList<Contact> contacts;

	public ContactProvider() {
		accountToRoster = FXCollections
				.observableMap(new ConcurrentHashMap<Account, Roster>());
		contacts = FXCollections
				.observableList(new CopyOnWriteArrayList<Contact>());
	}

	public void initRoster(Account account, Roster roster) {
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
				System.out.println(c);
				contacts.add(c);
			}
		}
	}

	public ObservableList<Contact> getData() {
		return contacts;
	}
}
