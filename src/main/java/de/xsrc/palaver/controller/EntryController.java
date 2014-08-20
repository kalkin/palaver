package de.xsrc.palaver.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import org.datafx.controller.FXMLController;

import de.xsrc.palaver.model.Entry;

@FXMLController("/fxml/EntryView.fxml")
public class EntryController {

	private Entry entry;

	@FXML
	private Label body;

	@FXML
	private Label from;

	public void setEntry(Entry e) {
		this.entry = e;
		body.textProperty().bind(entry.bodyProperty());
		from.setText(e.getFrom().substring(0, 1).toUpperCase());
	}

	@FXML
	private void initialize() {
	}

}
