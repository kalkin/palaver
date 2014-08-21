package de.xsrc.palaver.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import org.datafx.controller.FXMLController;
import org.jivesoftware.smack.util.StringUtils;

import de.xsrc.palaver.model.Entry;

@FXMLController("/fxml/EntryView.fxml")
public class EntryController {

	private Entry entry;

	@FXML
	private Label body;

	@FXML
	private Text from;
	
	@FXML
	private Rectangle r; 

	public void setEntry(Entry e) {
		this.entry = e;
		body.textProperty().bind(entry.bodyProperty());
		from.setText(e.getFrom().substring(0, 1).toUpperCase());
		String name = StringUtils.parseName(e.getFrom());
		int modena_colors[] = {0xf3622d, 0xfba71b, 0xFF673ab7, 0x41a9c9, 0x9a42c8, 0xc84164, 0xFF00bcd4, 0x888888};
		int color = modena_colors[(int) ((name.hashCode() & 0xffffffffl) % modena_colors.length)];
		String hexcolor =  String.format("#%06X", (0xFFFFFF & color));
		r.setStyle("-fx-background-color:" + hexcolor);
		r.setFill(Paint.valueOf(hexcolor));
	}

	@FXML
	private void initialize() {
	}

}
