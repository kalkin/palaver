package de.xsrc.palaver.beans;

import de.xsrc.palaver.utils.BoundPropertySupport;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.datafx.util.EntityWithId;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Represents a user account with jid and password
 *
 * @author kalkin
 */
@XmlRootElement(name = "credentials")
public class Credentials implements EntityWithId<String> {

    private static final long serialVersionUID = 1L;

    private StringProperty jid = new SimpleStringProperty();

    private StringProperty password = new SimpleStringProperty();

    private BoundPropertySupport credentialsChanged = new BoundPropertySupport(this);

    public Credentials() {
        this.credentialsChanged.raisePropertyChangeEventFor(jid);
        this.credentialsChanged.raisePropertyChangeEventFor(password);
    }

    @Override
    public int hashCode() {
        return getId().hashCode() * 23;
    }

    public String getId() {
        return getJid();
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public String getJid() {
        return jid.get();
    }

    public void setJid(String jid) {
        this.jid.set(jid);
    }

    public String toString() {
        return jid.get();
    }

    public BoundPropertySupport credentialsChangedProperty() {
        return this.credentialsChanged;
    }
}
