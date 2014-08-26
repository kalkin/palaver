package de.xsrc.palaver.controller;

import java.util.List;
import java.util.logging.Logger;

import javafx.application.Platform;
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
import org.datafx.controller.context.ApplicationContext;
import org.datafx.controller.context.ViewContext;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.util.StringUtils;

import de.xsrc.palaver.model.Entry;
import de.xsrc.palaver.model.Palaver;
import de.xsrc.palaver.provider.PalaverProvider;
import de.xsrc.palaver.utils.ColdStorage;
import de.xsrc.palaver.utils.Storage;
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

	@FXML
	private void initialize() {
		requestFocus();

	}

	public void setPalaver(Palaver p) {
		this.palaver = p;
		history = p.history.getEntryListProperty();
		add(history);
		history.addListener((Change<? extends Entry> change) -> {
			while (change.next()) {
				logger.finer("New Msgs were added to " + p);
				add(change.getAddedSubList());
				requestFocus();
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
				Platform.runLater(() -> historyBox.getChildren().add(
						context.getRootNode()));
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
		logger.finer(historyBox.toString());
		PalaverProvider provider = ApplicationContext.getInstance()
				.getRegisteredObject(PalaverProvider.class);
		// TODO Find out why write back handler does not handle this.
		ColdStorage.save(Palaver.class, provider.getData());
		String server = StringUtils.parseServer(palaver.getRecipient());
		if (server.startsWith("muc")) {
			try {
				ChatUtils.getMuc(palaver).sendMessage(e.getBody());
			} catch (NotConnectedException | XMPPException e1) {
				e1.printStackTrace();
			}
		} else {
			this.history.add(e);
			ChatUtils.sendMsg(palaver, e);
		}
		chatInput.clear();
	}

	public void requestFocus() {
		Platform.runLater(() -> {
			chatInput.requestFocus();
			chatInput.positionCaret(chatInput.getLength());
			Platform.runLater(() -> scrollPane.setVvalue(scrollPane.getVmax()));
		});

	}
}
