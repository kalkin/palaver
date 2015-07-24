package de.xsrc.palaver.models;

import de.xsrc.palaver.beans.Account;
import de.xsrc.palaver.beans.Contact;
import de.xsrc.palaver.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jxmpp.util.XmppStringUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

// Crosspoint between view, smack and ContactProvider (file backend)
public class ContactModel {
    private static final Logger logger = Logger.getLogger(ContactModel.class
            .getName());

    protected ObservableList<Contact> data = FXCollections.observableList(new CopyOnWriteArrayList<>());

    protected ObservableMap<Account, Roster> rosterMap = FXCollections.observableMap(new
            ConcurrentHashMap<>());

    public ObservableList<Contact> getData() {
        return FXCollections.unmodifiableObservableList(data);
    }


    public void addContact(Contact contact) {
        data.add(contact);
    }

    public void updateContact(Contact contact) {
        throw new UnsupportedOperationException();
    }

    public void removeContact(Contact contact) {
        data.remove(contact);
    }

    public void registerRoster(Account account, Roster roster) {
        rosterMap.put(account,roster);
    }

    public void subscribe(Account account, String jid) throws SmackException.NotLoggedInException, XMPPException.XMPPErrorException, SmackException.NotConnectedException, SmackException.NoResponseException {
        final Roster roster = rosterMap.get(account);
        roster.createEntry(jid, XmppStringUtils.parseLocalpart(jid), null);
    }

    public void unsubscribe(Account account, String jid) throws SmackException.NotLoggedInException, XMPPException
            .XMPPErrorException, SmackException.NotConnectedException, SmackException.NoResponseException {
        final Roster roster = rosterMap.get(account);
        final RosterEntry entry = roster.getEntry(jid);
        roster.removeEntry(entry);
    }
}
