package de.xsrc.palaver.controller;

import de.xsrc.palaver.beans.Entry;
import de.xsrc.palaver.beans.Palaver;
import de.xsrc.palaver.xmpp.PalaverManager;
import javafx.application.Platform;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.datafx.concurrent.ObservableExecutor;
import org.datafx.controller.FXMLController;
import org.datafx.controller.FxmlLoadException;
import org.datafx.controller.ViewFactory;
import org.datafx.controller.context.ApplicationContext;
import org.datafx.controller.context.ViewContext;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;

import java.util.List;
import java.util.logging.Logger;

@FXMLController("/fxml/HistoryView.fxml")
public class HistoryController {
	private static final Logger logger = Logger
					.getLogger(HistoryController.class.getName());
	@FXML
	private TextField chatInput;
	@FXML
	private VBox historyBox;
	@FXML
	private ScrollPane scrollPane;

	private Palaver Mpalaver;
	private ObservableList<Entry> history;
	private Palaver palaver;

	@FXML
	private void initialize() {
		requestFocus();

	}

	public void setPalaver(Palaver palaver) {
		this.palaver = palaver;
		history = palaver.history.entryListProperty();
		add(history);
		history.addListener((Change<? extends Entry> change) -> {
			while (change.next()) {
				logger.finer("New Messages were added to " + palaver);
				add(change.getAddedSubList());
			}
		});
		historyBox.heightProperty().addListener((observable, oldValue, newValue) -> {
			Platform.runLater(() -> scrollPane.setVvalue(scrollPane.getVmax()));
			palaver.setUnread(false);
		});
	}

	private void add(List<? extends Entry> list) {
		ObservableExecutor executor = ApplicationContext.getInstance().getRegisteredObject(ObservableExecutor.class);
		executor.submit(() -> {
							for (Entry entry : list) {
								Label l = new Label();
								l.textProperty().bind(entry.bodyProperty());

								final ViewContext<EntryController> context;
								try {
									context = ViewFactory.getInstance().createByController(
													EntryController.class);
									context.getController().setEntry(entry);
									Platform.runLater(() -> historyBox.getChildren().add(context.getRootNode()));
								} catch (FxmlLoadException e) {
									e.printStackTrace();
									logger.severe("Could not add entry from " + entry.getFrom() + " "
													+ entry);
								}
							}
						}
		);
	}

	@FXML
	private void sendMsgAction() throws NotConnectedException, XMPPException {
		String body = chatInput.getText();

		PalaverManager.sendMsg(this.palaver, body);
		chatInput.clear();
	}

	public void requestFocus() {
		Platform.runLater(() -> {
			chatInput.requestFocus();
			chatInput.positionCaret(chatInput.getLength());
		});

	}
}
