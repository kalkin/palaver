package de.xsrc.palaver.beans;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import org.datafx.util.EntityWithId;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "entry")
public class HistoryEntry implements EntityWithId<String> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private LongProperty receivedAt;
    private StringProperty body;
    private StringProperty from;
    private BooleanProperty sendState;

    public HistoryEntry() {
        this(null, null);

    }

    public HistoryEntry(String from, String body) {
        this.receivedAt = new SimpleLongProperty(System.currentTimeMillis());
        this.from = new SimpleStringProperty(from);
        this.body = new SimpleStringProperty(body);
        this.sendState = new SimpleBooleanProperty(false);
    }

    @Override
    public String getId() {
        return getReceivedAt() + "";
    }

    public Long getReceivedAt() {
        return receivedAt.get();
    }

    public void setReceivedAt(long l) {
        receivedAt.set(l);
    }

    public String toString() {
        return body.get();
    }

    public String getBody() {
        return body.get();
    }

    public void setBody(String body) {
        this.body.set(body);
    }

    public String getFrom() {
        return from.get();
    }

    public void setFrom(String from) {
        this.from.set(from);
    }

    public ObservableValue<? extends String> bodyProperty() {
        return body;
    }

    public boolean getSendState() {
        return sendState.get();
    }

    public BooleanProperty sendStateProperty() {
        return sendState;
    }

    public void setSendState(boolean sendState) {
        this.sendState.set(sendState);
    }
}
