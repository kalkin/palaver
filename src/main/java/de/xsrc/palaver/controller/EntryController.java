package de.xsrc.palaver.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import org.datafx.controller.FXMLController;

import de.xsrc.palaver.model.Entry;

@FXMLController("/fxml/EntryView.fxml")
public class EntryController {

	private Entry entry;

	@FXML
	private Label body;

	public void setEntry(Entry e) {
		this.entry = e;
		body.textProperty().bind(entry.bodyProperty());

	}

	@FXML
	private void initialize() {
	}

}
