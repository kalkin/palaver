package de.xsrc.palaver.controller;

import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import org.datafx.controller.FXMLController;

import de.xsrc.palaver.model.Entry;
import de.xsrc.palaver.model.Palaver;
import de.xsrc.palaver.utils.Storage;
import de.xsrc.palaver.utils.Utils;
import de.xsrc.palaver.xmpp.ChatUtils;

@FXMLController("/fxml/HistoryView.fxml")
public class HistoryController {
	@FXML
	private TextField chatInput;
	private static final Logger logger = Logger.getLogger(Storage.class
			.getName());
	@FXML
	private ListView<Entry> history;

	private Palaver palaver;

	public void setPalaver(Palaver p) {
			this.palaver = p;
		history.setItems(p.history.getEntryListProperty());
	}

	@FXML
	private void sendMsgAction() {
		Entry e = new Entry(palaver.getAccount(), chatInput.getText());
		this.history.getItems().add(e);
		logger.finer(history.toString());
		Utils.getStorage(Palaver.class).save(palaver);
		ChatUtils.sendMsg(palaver, e);
		chatInput.clear();
	}
}
