package de.xsrc.palaver.controller;

import javafx.scene.control.ListCell;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

import org.jivesoftware.smack.util.StringUtils;

import de.xsrc.palaver.model.Palaver;
import de.xsrc.palaver.xmpp.UiUtils;

public class PalaverCell extends ListCell<Palaver> {
	@Override
	public void updateItem(Palaver p, boolean empty) {
		super.updateItem(p, empty);
		if (!empty && p != null) {
			String name = StringUtils.parseName(p.getRecipient());
			if (name != null && name.length() > 0) {
				StackPane sp = UiUtils.getAvatar(name);
				setGraphic(sp);
				setText(StringUtils.parseName(p.getRecipient()));
				setFont(Font.font(23));
			}
		} else {
			setGraphic(null);
			setText(null);
		}
	}

}
