package de.xsrc.palaver.controller;

import de.xsrc.palaver.model.Entry;
import de.xsrc.palaver.xmpp.UiUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import org.datafx.controller.FXMLController;
import org.jivesoftware.smack.util.StringUtils;

@FXMLController("/fxml/EntryView.fxml")
public class EntryController {

	@FXML
	private Label body;

	public void setEntry(Entry e) {
		body.setText(e.getBody());
		if (e.getBody().length() > 5) {
			body.setWrapText(true);
		}
		String name = StringUtils.parseName(e.getFrom());
		StackPane avatar = UiUtils.getAvatar(name);
		body.setGraphic(avatar);
	}

	@FXML
	private void initialize() {
	}

}
