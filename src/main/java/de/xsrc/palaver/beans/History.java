package de.xsrc.palaver.beans;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class History {
    @XmlElement(name = "entry", type = HistoryEntry.class)
    private ListProperty<HistoryEntry> entryList;

    public History() {
        entryList = new SimpleListProperty<HistoryEntry>(
                FXCollections.observableArrayList());
    }

    public List<HistoryEntry> getEntryList() {
        return entryList.get();
    }

    public void setEntryList(ObservableList<HistoryEntry> history) {
        this.entryList.set(history);
    }

    public ObservableList<HistoryEntry> entryListProperty() {
        return entryList;
    }

    public void addEntry(HistoryEntry e) {
        this.entryList.add(e);
    }
}
