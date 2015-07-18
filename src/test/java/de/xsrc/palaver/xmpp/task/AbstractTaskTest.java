package de.xsrc.palaver.xmpp.task;

import de.xsrc.palaver.Connection;
import de.xsrc.palaver.NameGenerator;
import de.xsrc.palaver.beans.Credentials;
import de.xsrc.palaver.xmpp.exception.AccountCreationException;
import de.xsrc.palaver.xmpp.exception.AccountDeletionException;
import de.xsrc.palaver.xmpp.exception.ConnectionFailedException;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 01.07.15.
 */
public class AbstractTaskTest {

    static ListProperty<Credentials> getMockAccounts(int amount) {
        final ListProperty<Credentials> credentialsList = new SimpleListProperty<>(FXCollections.observableArrayList());
        for (int i = 0; i < amount; i++) {
            final Credentials credentials = getMockAccount();
            credentialsList.add(credentials);
            CreateAccountTask createAccountTask = new CreateAccountTask(credentials, getObservableMap());
            try {
                createAccountTask.call().close();
            } catch (AccountCreationException | ConnectionFailedException e) {
                e.printStackTrace();
            }
        }
        return credentialsList;
    }

    static Credentials getMockAccount() {
        final NameGenerator gen = new NameGenerator();
        return getAccount(gen.getName() + "@xsrc.de", "password");
    }


    static void removeMockAccounts(ListProperty<Credentials> credentialsList) {
        for (Credentials credentials : credentialsList) {
            try {
                final DeleteAccountTask deleteAccountTask = new DeleteAccountTask(credentials);
                deleteAccountTask.call();
            } catch (AccountDeletionException | ConnectionFailedException e) {
                e.printStackTrace();
            }
        }
    }

    static Credentials getAccount(String jid, String password) {
        Credentials credentials = new Credentials();
        credentials.setJid(jid);
        credentials.setPassword(password);
        return credentials;
    }

    static ObservableMap<Credentials, Connection> getObservableMap() {
        return FXCollections.observableMap(new ConcurrentHashMap<>());
    }
}
