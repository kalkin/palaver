package de.xsrc.palaver.model;

import java.util.List;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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
	
	
	private ListProperty<String> history;

	public Palaver() {
		this.recipient = new SimpleStringProperty();
		this.account = new SimpleStringProperty();
		this.history = new SimpleListProperty<String>();
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

	public List<String> getHistory() {
		return history.get();
	}

	public void setRecipient(String s) {
		recipient.set(s);
	}

	public void setAccount(String s) {
		account.set(s);
	}

	public void setHistory(List<String> s) {
		history.setAll(s);
	}

	public void setAccount(Account a) {
		account.set(a.getId());
		
	}
	
	public String toString(){
		return getId();
	}
}
