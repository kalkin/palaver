package de.xsrc.palaver.listeners;

import de.xsrc.palaver.beans.Account;
import de.xsrc.palaver.models.ContactModel;
import de.xsrc.palaver.xmpp.ConnectionManager;
import de.xsrc.palaver.xmpp.model.Contact;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;

import java.util.Collection;
import java.util.logging.Logger;

public class PalaverRosterListener implements RosterListener {

	private static final Logger logger = Logger.getLogger(PalaverRosterListener.class
					.getName());

	private Account account;


	public PalaverRosterListener(Account account) {
		this.account = account;
		logger.fine(String.format("Created %s", account.getJid()));
	}

	@Override
	public void entriesAdded(Collection<String> addresses) {
		logger.fine(String.format("Roster %s added %s", account.getJid(), addresses.toString()));

		for (String address : addresses) {
				Contact contact = getContact(address);
			ContactModel.getInstance().addContact(contact);
		}

	}

	@Override
	public void entriesUpdated(Collection<String> addresses) {

	}

	@Override
	public void entriesDeleted(Collection<String> addresses) {
		logger.fine(String.format("Roster %s deleted %s", account.getJid(), addresses));
		for (String address : addresses) {
			Contact contact = getContact(address);
			ContactModel.getInstance().removeContact(contact);
		}

	}

	@Override
	public void presenceChanged(Presence presence) {

	}


	private Contact getContact(String address) {
		RosterEntry entry = ConnectionManager.getConnection(this.account).getRoster().getEntry(address);
		Contact contact = new Contact();
		contact.setConference(false);
		contact.setAccount(account.getJid());
		contact.setJid(entry.getUser());
		contact.setName(entry.getName());
		return contact;

	}
}
