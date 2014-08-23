package de.xsrc.palaver.xmpp.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Buddy {

	private StringProperty name;
	private StringProperty jid;
	private StringProperty account;

	public Buddy() {
		this.name = new SimpleStringProperty();
		this.jid = new SimpleStringProperty();
		this.account = new SimpleStringProperty();
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
}
