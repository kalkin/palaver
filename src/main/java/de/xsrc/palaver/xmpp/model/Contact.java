package de.xsrc.palaver.xmpp.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Contact {

	private StringProperty name;
	private StringProperty jid;
	private StringProperty account;
	private BooleanProperty conference;

	public Contact() {
		this.name = new SimpleStringProperty();
		this.jid = new SimpleStringProperty();
		this.account = new SimpleStringProperty();
		this.conference = new SimpleBooleanProperty(false);
	}

	public String getJid() {
		return jid.get();
	}

	public void setJid(String jid) {
		this.jid.set(jid);
	}

	public String getAccount() {
		return account.get();
	}

	public void setAccount(String account) {
		this.account.set(account);
	}

	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public String toString() {
		return getName();
	}

	@Override
	public boolean equals(Object o) {

		if (o == this)
			return true;

		if (!(o instanceof Contact))
			return false;
		Contact c = (Contact) o;
		return c.getJid().equals(getJid()) && c.getAccount().equals(getAccount());

	}

	@Override
	public int hashCode(){
		return (getAccount() + ":" + getJid()).hashCode();
	}

	public boolean getConference() {
		return conference.get();
	}

	public void setConference(boolean conference) {
		this.conference.setValue(conference);
	}

	public boolean isConference() {
		return getConference();
	}
}
