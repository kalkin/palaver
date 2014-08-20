package de.xsrc.palaver.controller;

import java.util.List;
import java.util.logging.Logger;

import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import org.datafx.controller.FXMLController;
import org.datafx.controller.FxmlLoadException;
import org.datafx.controller.ViewFactory;
import org.datafx.controller.context.ViewContext;

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
	private VBox historyBox;
	@FXML
	private ScrollPane scrollPane;

	private Palaver palaver;
	private ObservableList<Entry> history;

	public void setPalaver(Palaver p) {
		this.palaver = p;
		history = p.history.getEntryListProperty();
		add(history);

		history.addListener((Change<? extends Entry> change) -> {
			while (change.next()) {
				logger.finer("New Msgs were added to " + palaver);
				add(change.getAddedSubList());

			}
		});
	}

	private void add(List<? extends Entry> list) {
		for (Entry entry : list) {
			Label l = new Label();
			l.textProperty().bind(entry.bodyProperty());

			ViewContext<EntryController> context;
			try {
				context = ViewFactory.getInstance().createByController(
						EntryController.class);
				context.getController().setEntry(entry);
				historyBox.getChildren().add(context.getRootNode());
				scrollPane.setVvalue(scrollPane.getVmax());
			} catch (FxmlLoadException e) {
				e.printStackTrace();
				logger.severe("Could not add entry from " + entry.getFrom()
						+ " " + entry);
			}
		}
	}

	@FXML
	private void sendMsgAction() {
		Entry e = new Entry(palaver.getAccount(), chatInput.getText());
		this.history.add(e);
		logger.finer(historyBox.toString());
		Utils.getStorage(Palaver.class).save(palaver);
		ChatUtils.sendMsg(palaver, e);
		chatInput.clear();
	}
}
