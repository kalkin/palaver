package de.xsrc.palaver.xmpp.task;

import de.xsrc.palaver.NameGenerator;
import de.xsrc.palaver.beans.Credentials;
import de.xsrc.palaver.xmpp.exception.AccountCreationException;
import de.xsrc.palaver.xmpp.exception.AccountDeletionException;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Bahtiar `kalkin-` Gadimov on 01.07.15.
 */
public class AbstractTaskTest {

    public static ListProperty<Credentials> getMockAccounts(int amount) {
        final ListProperty<Credentials> credentialses = new SimpleListProperty<>(FXCollections.observableArrayList());
        for (int i = 0; i < amount; i++) {
            final Credentials credentials = getMockAccount();
            credentialses.add(credentials);
            CreateAccountTask createAccountTask = new CreateAccountTask(credentials, getObservableMap());
            try {
                createAccountTask.call().disconnect();
            } catch (AccountCreationException e) {
                e.printStackTrace();
            }
        }
        return credentialses;
    }

    protected static Credentials getMockAccount() {
        final NameGenerator gen = new NameGenerator();
        final Credentials credentials = new Credentials();
        credentials.setJid(gen.getName() + "@xsrc.de");
        credentials.setPassword("password");
        return credentials;
    }

    protected static ObservableMap<Credentials, XMPPTCPConnection> getObservableMap(){
        return FXCollections.observableMap(new ConcurrentHashMap<Credentials, XMPPTCPConnection>());
    }

    public static void removeMockAccounts(ListProperty<Credentials> credentialses) {
        for (Credentials credentials : credentialses) {
            try {
                final DeleteAccountTask deleteAccountTask = new DeleteAccountTask(credentials);
                deleteAccountTask.call();
            } catch (AccountDeletionException e) {
                e.printStackTrace();
            }
        }
    }

}
