package de.xsrc.palaver.controller;

import javafx.scene.control.ListCell;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import de.xsrc.palaver.xmpp.UiUtils;
import de.xsrc.palaver.xmpp.model.Buddy;

public class BuddyCell extends ListCell<Buddy> {
	@Override
	public void updateItem(Buddy buddy, boolean empty) {
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