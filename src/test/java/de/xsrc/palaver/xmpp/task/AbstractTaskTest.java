package de.xsrc.palaver.xmpp.task;

import de.xsrc.palaver.NameGenerator;
import de.xsrc.palaver.beans.Account;
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

    public static ListProperty<Account> getMockAccounts(int amount) {
        final ListProperty<Account> accounts = new SimpleListProperty<>(FXCollections.observableArrayList());
        for (int i = 0; i < amount; i++) {
            final Account account = getMockAccount();
            accounts.add(account);
            CreateAccountTask createAccountTask = new CreateAccountTask(account, getObservableMap());
            try {
                createAccountTask.call().disconnect();
            } catch (AccountCreationException e) {
                e.printStackTrace();
            }
        }
        return accounts;
    }

    protected static Account getMockAccount() {
        final NameGenerator gen = new NameGenerator();
        final Account account = new Account();
        account.setJid(gen.getName() + "@xsrc.de");
        account.setPassword("password");
        return account;
    }

    protected static ObservableMap<Account, XMPPTCPConnection> getObservableMap(){
        return FXCollections.observableMap(new ConcurrentHashMap<Account, XMPPTCPConnection>());
    }

    public static void removeMockAccounts(ListProperty<Account> accounts) {
        for (Account account : accounts) {
            try {
                final DeleteAccountTask deleteAccountTask = new DeleteAccountTask(account);
                deleteAccountTask.call();
            } catch (AccountDeletionException e) {
                e.printStackTrace();
            }
        }
    }

}
