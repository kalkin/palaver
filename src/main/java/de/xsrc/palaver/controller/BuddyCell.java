package de.xsrc.palaver.controller;

import de.xsrc.palaver.utils.UiUtils;
import de.xsrc.palaver.beans.Contact;
import javafx.scene.control.ListCell;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

public class BuddyCell extends ListCell<Contact> {
	@Override
	public void updateItem(Contact buddy, boolean empty) {
		super.updateItem(buddy, empty);
		if (!empty && buddy != null && buddy.getName().length() > 0) {
			String name = buddy.getName();
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
