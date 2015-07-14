package de.xsrc.palaver.xmpp.task;

import de.xsrc.palaver.beans.Credentials;
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
    protected static Credentials getAccount(String jid, String password) {
        Credentials credentials = new Credentials();
        credentials.setJid(jid);
        credentials.setPassword(password);
        return credentials;
    }

    protected static ObservableMap<Credentials, XMPPTCPConnection> getObservableMap(){
        return FXCollections.observableMap(new ConcurrentHashMap<Credentials, XMPPTCPConnection>());
    }
}
