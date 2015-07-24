package de.xsrc.palaver.xmpp.listeners;

import de.xsrc.palaver.beans.Account;
import de.xsrc.palaver.beans.Contact;
import de.xsrc.palaver.models.ContactModel;
import de.xsrc.palaver.utils.Utils;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.roster.RosterListener;

import java.util.Collection;
import java.util.logging.Logger;

public class PalaverRosterListener implements RosterListener {

    private static final Logger logger = Logger.getLogger(PalaverRosterListener.class
            .getName());

    private Account account;
    private ContactModel contacts;
    private Roster roster;


    public PalaverRosterListener(Account account, ContactModel contactModel, Roster roster) {
        this.account = account;
        this.contacts = contactModel;
        this.roster = roster;
        logger.fine(String.format("Created %s", account.getJid()));
    }

    @Override
    public void entriesAdded(Collection<String> addresses) {
        logger.fine(String.format("Roster %s added %s", account.getJid(), addresses.toString()));

        for (String address : addresses) {
            Contact contact = getContact(address);
            contacts.addContact(contact);
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
            if (contact == null) {
                return;
            }
            contacts.removeContact(contact);
        }

    }

    @Override
    public void presenceChanged(Presence presence) {

    }


    private Contact getContact(String address) {
        RosterEntry entry = roster.getEntry(address);
        if (entry == null) {
            return null;
        }
        return Utils.createContact(account.getJid(), address, entry.getName(), false);
    }
}
