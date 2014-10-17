package de.xsrc.palaver.controller;

import de.xsrc.palaver.beans.Palaver;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.OverrunStyle;
import org.jivesoftware.smack.util.StringUtils;

public class PalaverCell extends ListCell<Palaver> {
	@Override
	public void updateItem(Palaver p, boolean empty) {
		super.updateItem(p, empty);
		if (!empty && p != null) {
			String name = StringUtils.parseName(p.getRecipient());
			if (name != null && name.length() > 0) {
				Label label = new Label();
				if (p.isConference()) {
					label.getStyleClass().add("muc");
				} else {
					label.getStyleClass().add("chat");

				}

				label.setTextOverrun(OverrunStyle.ELLIPSIS);
				label.setText(StringUtils.parseName(p.getRecipient()));

				setGraphic(label);


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
			Platform.runLater(() -> this.setStyle("-fx-font-weight: bold"));
		} else {
			Platform.runLater(() -> this.setStyle("-fx-font-weight: normal"));
		}
	}

}
