package de.xsrc.palaver;

import de.xsrc.palaver.beans.Contact;
import de.xsrc.palaver.beans.Credentials;
import de.xsrc.palaver.xmpp.exception.AccountCreationException;
import de.xsrc.palaver.xmpp.exception.AccountDeletionException;
import de.xsrc.palaver.xmpp.exception.ConnectionException;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 01.07.15.
 */
public class AbstractTest {

    protected static ListProperty<Credentials> createMockAccounts(int amount) {
        final ListProperty<Credentials> credentialsList = new SimpleListProperty<>(FXCollections.observableArrayList());
        for (int i = 0; i < amount; i++) {
            final Credentials credentials = getMockCredentials();
            try {
                registerMockAccount(credentials);
            } catch (ConnectionException | AccountCreationException e) {
                e.printStackTrace();
                // Clean up
                removeMockAccounts(credentialsList);
                throw new RuntimeException("Something went wrong while registering mock accounts");
            }
            credentialsList.add(credentials);
        }
        return credentialsList;
    }

    protected static void registerMockAccount(Credentials credentials) throws ConnectionException,
            AccountCreationException {
        final Connection connection = new Connection(credentials);
        connection.register();
        connection.close();
    }

    protected static Credentials getMockCredentials() {
        final NameGenerator gen = new NameGenerator();
        return getAccount(gen.getName() + "@xsrc.de", "password");
    }


    protected static void removeMockAccounts(ListProperty<Credentials> credentialsList) {
        credentialsList.forEach(de.xsrc.palaver.AbstractTest::removeMockAccount);
    }

    protected static void removeMockAccount(Credentials credentials) {
        final Connection connection = new Connection(credentials);
        try {
            connection.delete();
        } catch (AccountDeletionException | ConnectionException e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
    }

    protected static Credentials getAccount(String jid, String password) {
        Credentials credentials = new Credentials();
        credentials.setJid(jid);
        credentials.setPassword(password);
        return credentials;
    }

    protected static ObservableMap<String, Connection> getObservableMap() {
        return FXCollections.observableMap(new ConcurrentHashMap<>());
    }

    protected static Contact getMockContact(){
        Credentials juliaCredentials = getMockCredentials();
        Credentials romeoCredentials = getMockCredentials();
        return getMockContact(romeoCredentials.getJid(), juliaCredentials.getJid());
    }

    protected static Contact getMockContact(String account, String jid) {
        final Contact contact = new Contact();
        contact.setAccount(account);
        contact.setJid(jid);
        contact.setConference(false);
        return contact;
    }
}
