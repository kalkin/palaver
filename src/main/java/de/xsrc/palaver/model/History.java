package de.xsrc.palaver.model;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class History {
	@XmlElement(name = "entry", type = Entry.class)
	private ListProperty<Entry> entryList;

	public History() {
		entryList = new SimpleListProperty<Entry>(
						FXCollections.observableArrayList());
	}

	public List<Entry> getEntryList() {
		return entryList.get();
	}

	public void setEntryList(ObservableList<Entry> history) {
		this.entryList.set(history);
	}

	public ObservableList<Entry> entryListProperty() {
		return entryList;
	}

	public void addEntry(Entry e) {
		this.entryList.add(e);
	}
}
