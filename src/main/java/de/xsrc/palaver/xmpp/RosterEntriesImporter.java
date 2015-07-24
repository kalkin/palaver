package de.xsrc.palaver.xmpp;

import de.xsrc.palaver.beans.Account;
import de.xsrc.palaver.beans.Contact;
import de.xsrc.palaver.models.ContactModel;
import de.xsrc.palaver.utils.Utils;
import org.jivesoftware.smack.roster.RosterEntry;

import java.util.Collection;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 30.06.15.
 */
public class RosterEntriesImporter implements org.jivesoftware.smack.roster.RosterEntries {

    private Account account;
    private ContactModel contactModel;

    public RosterEntriesImporter(Account account, ContactModel contactModel) {
        this.account = account;
        this.contactModel = contactModel;
    }

    @Override
    public void rosterEntires(Collection<RosterEntry> rosterEntries) {
        for (RosterEntry entry: rosterEntries) {
            Contact contact = Utils.createContact(account.getJid(), entry.getUser(), entry.getName(), false);
            contactModel.addContact(contact);
        }
    }
}
