package de.xsrc.palaver.beans;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.datafx.util.EntityWithId;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.logging.Logger;

@XmlRootElement(name = "conversation")
public class Conversation implements EntityWithId<String> {
    private static final Logger logger = Logger.getLogger(Conversation.class.getName());

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "history", type = History.class)
    public History history;
    /**
     * Who should receive user send the msgs
     */
    private StringProperty recipient;
    /**
     * Which account should send msgs
     */
    private StringProperty account;
    private BooleanProperty unread;
    private BooleanProperty closed;
    private BooleanProperty conference;


    public Conversation() {
        this.recipient = new SimpleStringProperty();
        this.account = new SimpleStringProperty();
        this.history = new History();
        this.closed = new SimpleBooleanProperty(false);
        this.unread = new SimpleBooleanProperty(false);
        this.conference = new SimpleBooleanProperty(false);
    }

    public Conversation(String account, String recipient) {
        this.recipient = new SimpleStringProperty(recipient);
        this.account = new SimpleStringProperty(account);
        this.history = new History();
        this.closed = new SimpleBooleanProperty(false);
        this.unread = new SimpleBooleanProperty(false);
        this.conference = new SimpleBooleanProperty(false);
    }


    public String getId() {
        return account.get() + ":" + recipient.get();
    }

    public String getRecipient() {
        return recipient.get();
    }

    public void setRecipient(String s) {
        recipient.set(s);
    }

    public String getAccount() {
        return account.get();
    }

    public void setAccount(Credentials a) {
        account.set(a.getId());

    }

    public void setAccount(String s) {
        account.set(s);
    }

    @Override
    public String toString() {
        return getId();
    }

    public void add(HistoryEntry historyEntry) {
        history.addEntry(historyEntry);
    }

    public boolean getClosed() {
        return this.closed.get();
    }

    public void setClosed(boolean b) {
        this.closed.set(b);
    }

    public BooleanProperty closedProperty() {
        return closed;
    }

    public boolean getUnread() {
        return unread.get();
    }

    public void setUnread(boolean unread) {
        this.unread.set(unread);
    }

    public BooleanProperty unreadProperty() {
        return unread;
    }

    public boolean getConference() {
        return conference.get();
    }

    public boolean isOpen() {
        return !closed.get();
    }

    public boolean isConference() {
        return getConference();
    }

    public void setConference(boolean conference) {
        this.conference.set(conference);
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode() * 23;
    }

    /**
     * Appends a message to the history.
     *
     * @param body      Text of the message
     * @param sendState If true this message does not need to be send. (Probably received over network, or carbon copy)
     */
    public void addMessage(String fromJid, String body, boolean sendState) {
        final HistoryEntry historyEntry = new HistoryEntry(fromJid, body);
        historyEntry.setSendState(sendState);
        this.history.addEntry(historyEntry);
        this.setClosed(false);
    }

}
