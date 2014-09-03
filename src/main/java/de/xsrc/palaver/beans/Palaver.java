package de.xsrc.palaver.beans;

import de.xsrc.palaver.provider.AccountProvider;
import javafx.beans.property.*;
import org.datafx.controller.context.ApplicationContext;
import org.datafx.util.EntityWithId;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "palaver")
public class Palaver implements EntityWithId<String> {

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
	private BooleanProperty conference = new SimpleBooleanProperty(false);


	public Palaver() {
		this.recipient = new SimpleStringProperty();
		this.account = new SimpleStringProperty();
		this.history = new History();
		this.closed = new SimpleBooleanProperty(false);
		this.unread = new SimpleBooleanProperty(false);

	}

	public Palaver(String account, String recipient) {
		this.recipient = new SimpleStringProperty(recipient);
		this.account = new SimpleStringProperty(account);
		this.history = new History();
		this.closed = new SimpleBooleanProperty(false);
		this.unread = new SimpleBooleanProperty(false);
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

	public void setAccount(String s) {
		account.set(s);
	}

	public void setAccount(Account a) {
		account.set(a.getId());

	}

	@Override
	public String toString() {
		return getId();
	}

	public void add(Entry entry) {
		history.addEntry(entry);
	}

	public boolean getClosed() {
		return this.closed.get();
	}

	public void setClosed(boolean b) {
		this.closed.set(b);
	}

	public boolean equals(Palaver p) {
		return p.getId().equals(getId());
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

	public Account accountInstance() throws IllegalStateException {
		ListProperty<Account> accounts = ApplicationContext.getInstance().getRegisteredObject(AccountProvider.class).getData();
		for (Account account : accounts.get())
			if (account.getJid().equalsIgnoreCase(getAccount())) {
				return account;
			}
		throw new IllegalStateException("Account with jid " + getAccount() + "does not exists");
	}

	public Boolean getConference() {
		return conference.get();
	}

	public void setConference(Boolean conference) {
		this.conference.set(conference);
	}
}
