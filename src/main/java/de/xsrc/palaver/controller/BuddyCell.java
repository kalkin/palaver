package de.xsrc.palaver.controller;

import javafx.scene.control.ListCell;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import de.xsrc.palaver.xmpp.UiUtils;

public class BuddyCell extends ListCell<String> {
	@Override
	public void updateItem(String name, boolean empty) {
		super.updateItem(name, empty);
		if (!empty && name != null && name.length() > 0) {
			StackPane sp = UiUtils.getAvatar(name);
			setGraphic(sp);
			setText(name);
			setFont(Font.font(23));
		} else {
			setGraphic(null);
			setText(null);
		}
	}

}
