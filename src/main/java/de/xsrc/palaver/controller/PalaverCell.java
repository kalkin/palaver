package de.xsrc.palaver.controller;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import de.xsrc.palaver.model.Palaver;

public class PalaverCell extends ListCell<Palaver> {
	@Override
	public void updateItem(Palaver p, boolean empty) {
		super.updateItem(p, empty);
		if (p != null) {
			Label l = new Label();
			l.setText(p.getRecipient().substring(0, 1));
			setGraphic(l);
			setText(p.getRecipient());
		}
	}
}
