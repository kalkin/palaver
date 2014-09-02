package de.xsrc.palaver.controller;

import de.xsrc.palaver.beans.Palaver;
import de.xsrc.palaver.xmpp.UiUtils;
import javafx.application.Platform;
import javafx.scene.control.ListCell;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.jivesoftware.smack.util.StringUtils;

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
				changeUnreadState(p.getUnread());
				p.unreadProperty().addListener((observable, oldValue, newValue) -> {
					changeUnreadState(newValue);
				});
			}
		} else {
			setGraphic(null);
			setText(null);
		}
	}

	private void changeUnreadState(boolean unread) {
		if (unread) {
			Platform.runLater(() -> this.setFont(Font.font(null, FontWeight.BOLD, 23)));
		} else {
			Platform.runLater(() -> this.setFont(Font.font(null, FontWeight.NORMAL, 23)));
		}
	}

}
