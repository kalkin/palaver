package de.xsrc.palaver.xmpp.listeners;

import de.xsrc.palaver.beans.Credentials;
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

    private final Credentials credentials;
    private final ContactModel contacts;
    private final Roster roster;


    public PalaverRosterListener(Credentials credentials, ContactModel contactModel, Roster roster) {
        this.credentials = credentials;
        this.contacts = contactModel;
        this.roster = roster;
        logger.fine(String.format("Created RosterListener for %s", credentials.getJid()));
    }

    @Override
    public void entriesAdded(Collection<String> addresses) {
        for (String address : addresses) {
            logger.finer(String.format("Roster %s add %s", credentials.getJid(), address));
            Contact contact = getContact(address);
            contacts.addContact(contact);
        }
    }

    @Override
    public void entriesUpdated(Collection<String> addresses) {

    }

    @Override
    public void entriesDeleted(Collection<String> addresses) {
        for (String address : addresses) {
            logger.finer(String.format("Roster %s delete %s", credentials.getJid(), address));
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
        return Utils.createContact(credentials.getJid(), address, entry.getName(), false);
    }
}
