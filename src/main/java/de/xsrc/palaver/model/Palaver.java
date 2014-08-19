package de.xsrc.palaver.model;

import java.util.List;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.datafx.util.EntityWithId;

@XmlRootElement(name = "palaver")
public class Palaver implements EntityWithId<String> {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Who should receive  user send the msgs 
	 */
	private StringProperty recipient;
	
	
	/**
	 * Which account  should send msgs
	 */
	private StringProperty account;
	
	@XmlElement(name = "history-entry", type = HistoryEntry.class)
	private ListProperty<HistoryEntry> history;

	public Palaver() {
		this.recipient = new SimpleStringProperty();
		this.account = new SimpleStringProperty();
		this.history = new SimpleListProperty<HistoryEntry>();
	}

	public String getId() {
		return account.get() + ":" + recipient.get();
	}

	public String getRecipient() {
		return recipient.get();
	}

	public String getAccount() {
		return account.get();
	}
	
	public List<HistoryEntry> getHistory(){
		return history.get();
	}


	public void setRecipient(String s) {
		recipient.set(s);
	}

	public void setAccount(String s) {
		account.set(s);
	}

	public void setHistory(ObservableList<HistoryEntry> s) {
		history.set(s);
	}

	public void setAccount(Account a) {
		account.set(a.getId());
		
	}
	
	public String toString(){
		return getId();
	}
}
