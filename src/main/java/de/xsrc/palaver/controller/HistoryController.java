package de.xsrc.palaver.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import org.datafx.controller.FXMLController;

import de.xsrc.palaver.model.HistoryEntry;
import de.xsrc.palaver.model.Palaver;
import de.xsrc.palaver.utils.Storage;
import de.xsrc.palaver.utils.Utils;

@FXMLController("/fxml/HistoryView.fxml")
public class HistoryController {
	@FXML
	private TextField chatInput;
	private static final Logger logger = Logger.getLogger(Storage.class
			.getName());
	@FXML
	private ListView<HistoryEntry> history;

	private Palaver palaver;

	public void setPalaver(Palaver p) {
		List<HistoryEntry> f = p.getHistory();
		this.palaver = p;
		ObservableList<HistoryEntry> historyList;
		if (f == null) {
			historyList = FXCollections.observableList(new LinkedList<HistoryEntry>());
		} else {
			historyList = FXCollections.observableList(f);
		}

		history.setItems(historyList);
	}

	@FXML
	private void sendMsgAction() {
		HistoryEntry h = new HistoryEntry(palaver.getAccount(), chatInput.getText());
		ObservableList<HistoryEntry> hList = history.getItems();
		hList.add(h);
		palaver.setHistory(hList);
		logger.warning(history.toString());
		Utils.getStorage(Palaver.class).save(palaver);
		chatInput.clear();
	}
}
