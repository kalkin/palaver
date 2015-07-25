package de.xsrc.palaver.models;

import de.xsrc.palaver.beans.Contact;
import de.xsrc.palaver.beans.Credentials;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.ObservableSet;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jxmpp.util.XmppStringUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

// Crosspoint between view, smack and ContactProvider (file backend)
public class ContactManager {
    private static final Logger logger = Logger.getLogger(ContactManager.class
            .getName());

    private final ObservableSet<Contact> data = FXCollections.observableSet(new HashSet<>());

    private final ObservableMap<String, Roster> rosterMap = FXCollections.observableMap(new
            ConcurrentHashMap<>());

    public static Contact createContact(String account, String jid, String name, Boolean conference) {
        Contact contact = new Contact();
        contact.setAccount(account);
        contact.setJid(jid);
        if (name != null && name.length() > 0) {
            contact.setName(name);
        } else {
            contact.setName(XmppStringUtils.parseLocalpart(contact.getJid()));
        }
        if (conference) {
            contact.setConference(true);
        }
        return contact;
    }
    public ObservableList<Contact> getData() {
        final LinkedList<Contact> contactLinkedList = new LinkedList<>(data);
        return FXCollections.observableList(contactLinkedList);
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

    public void registerRoster(String jid, Roster roster) {
        rosterMap.put(jid, roster);
    }

    public void subscribe(Credentials credentials, String jid) throws SmackException.NotLoggedInException, XMPPException.XMPPErrorException, SmackException.NotConnectedException, SmackException.NoResponseException {
        logger.finer("Adding " + jid + " to " + credentials.getJid() + "account");
        final Roster roster = rosterMap.get(credentials.getJid());
        roster.createEntry(jid, XmppStringUtils.parseLocalpart(jid), null);
    }

    public void unsubscribe(Credentials credentials, String jid) throws SmackException.NotLoggedInException, XMPPException
            .XMPPErrorException, SmackException.NotConnectedException, SmackException.NoResponseException {
        logger.finer("Removing " + jid + " from " + credentials.getJid() + "account");
        final Roster roster = rosterMap.get(credentials.getJid());
        final RosterEntry entry = roster.getEntry(jid);
        roster.removeEntry(entry);
    }
}
