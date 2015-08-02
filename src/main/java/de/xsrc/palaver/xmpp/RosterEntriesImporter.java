package de.xsrc.palaver.xmpp;

import de.xsrc.palaver.beans.Contact;
import de.xsrc.palaver.models.ContactManager;
import org.jivesoftware.smack.roster.RosterEntry;

import java.util.Collection;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 30.06.15.
 */
public class RosterEntriesImporter implements org.jivesoftware.smack.roster.RosterEntries {

    private String jid;
    private ContactManager contactManager;

    public RosterEntriesImporter(String jid, ContactManager contactManager) {
        this.jid = jid;
        this.contactManager = contactManager;
    }

    @Override
    public void rosterEntires(Collection<RosterEntry> rosterEntries) {
        for (RosterEntry entry: rosterEntries) {
            Contact contact = ContactManager.createContact(jid, entry.getUser(), entry.getName(), false);
            contactManager.addContact(contact);
        }
    }
}
