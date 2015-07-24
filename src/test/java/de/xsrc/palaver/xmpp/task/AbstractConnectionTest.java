package de.xsrc.palaver.xmpp.task;

import de.xsrc.palaver.beans.Account;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.util.concurrent.ConcurrentHashMap;

/**
 * <b>WARNING!</b> This class should not be confused with a test for {@link de.xsrc.palaver.xmpp.task.AbstractConnectionTask}.
 * This class just contains methods which are usefull for {@link CreateAccountTaskTest} and
 * {@link ConnectTaskTest}.
 * <p>
 * <p>
 * Created by Bahtiar `kalkin-` Gadimov on 28.06.15.
 */
public abstract class AbstractConnectionTest {
    protected static Account getAccount(String jid, String password) {
        Account account = new Account();
        account.setJid(jid);
        account.setPassword(password);
        return account;
    }

    protected static ObservableMap<Account, XMPPTCPConnection> getObservableMap(){
        return FXCollections.observableMap(new ConcurrentHashMap<Account, XMPPTCPConnection>());
    }
}
