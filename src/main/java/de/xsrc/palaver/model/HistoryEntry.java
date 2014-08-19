package de.xsrc.palaver.model;

import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.xml.bind.annotation.XmlRootElement;

import org.datafx.util.EntityWithId;

@XmlRootElement(name = "history-entry")
public class HistoryEntry implements EntityWithId<String> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private LongProperty receivedAt;
	private StringProperty body;
	private StringProperty from;

	public HistoryEntry() {
		this(null, null);

	}

	public HistoryEntry(String from, String body) {
		receivedAt = new SimpleLongProperty(System.currentTimeMillis());
		this.from = new SimpleStringProperty(from);
		this.body = new SimpleStringProperty(body);

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

}
