package de.xsrc.palaver.models;

import de.xsrc.palaver.xmpp.model.Contact;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

// Crosspoint between view, smack and ContactProvider (file backend)
public class ContactModel {

	protected static ObservableList<Contact> data = FXCollections.observableList(new CopyOnWriteArrayList<>());

	protected static ObservableMap<String, Contact> accountContactObservableMap = FXCollections.observableMap(new ConcurrentHashMap<>());

	private ContactModel() {
		accountContactObservableMap.addListener((MapChangeListener<String, Contact>) change -> {
			if (change.wasAdded()) {
				data.addAll(change.getValueAdded());
			} else if (change.wasRemoved()) {
				data.removeAll(change.getValueRemoved());
			}
		});

	}

	public static ContactModel getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public ObservableList<Contact> getData() {
		return FXCollections.unmodifiableObservableList(data);
	}


	public void addContact(Contact contact) {
		accountContactObservableMap.put(contact.getId(), contact);
	}

	public void updateContact(Contact contact) {
		throw new NotImplementedException();
	}

	public void removeContact(Contact contact) {
		accountContactObservableMap.remove(contact.getId());
	}

	private static final class InstanceHolder {
		static final ContactModel INSTANCE = new ContactModel();
	}


}
