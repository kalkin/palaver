package de.xsrc.palaver.controller;

import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

import org.jivesoftware.smack.util.StringUtils;

import de.xsrc.palaver.model.Palaver;

public class PalaverCell extends ListCell<Palaver> {
	@Override
	public void updateItem(Palaver p, boolean empty) {
		super.updateItem(p, empty);
		if (p != null) {
			Label l = new Label();
			l.setText(p.getRecipient().substring(0, 1));
			String name = StringUtils.parseName(p.getRecipient());
			int modena_colors[] = {0xf3622d, 0xfba71b, 0xFF673ab7, 0x41a9c9, 0x9a42c8, 0xc84164, 0xFF00bcd4, 0x888888};
			int color = modena_colors[(int) ((name.hashCode() & 0xffffffffl) % modena_colors.length)];
			String hexcolor =  String.format("#%06X", (0xFFFFFF & color));
			l.setStyle("-fx-background-color:" + hexcolor);
			setGraphic(l);
			setText(p.getRecipient());
		}
	}
}
