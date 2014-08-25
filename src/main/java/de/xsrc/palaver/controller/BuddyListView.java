package de.xsrc.palaver.controller;

import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import org.datafx.controller.FXMLController;
import org.datafx.controller.flow.FlowException;
import org.datafx.controller.flow.action.BackAction;
import org.datafx.controller.flow.context.FXMLViewFlowContext;
import org.datafx.controller.flow.context.ViewFlowContext;
import org.datafx.controller.util.VetoException;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.xsrc.palaver.xmpp.ChatUtils;
import de.xsrc.palaver.xmpp.UiUtils;
import de.xsrc.palaver.xmpp.model.Buddy;

@FXMLController("/fxml/BuddyListView.fxml")
public class BuddyListView {

	@FXML
	@BackAction
	private Button back;

	@FXML
	private Button addBuddy;

	@FXMLViewFlowContext
	private ViewFlowContext context;

	@FXML
	private Label faSearch;

	@FXML
	private TextField searchInput;

	@FXML
	private ListView<Buddy> list;

	private static final Logger logger = Logger.getLogger(BuddyListView.class
			.getName());

	@SuppressWarnings("unused")
	private void initialize() {
		AwesomeDude.setIcon(back, AwesomeIcon.CHEVRON_LEFT, "20");

		HBox hbox = new HBox();
		hbox.setAlignment(Pos.CENTER);

		hbox.getChildren().add(
				AwesomeDude.createIconLabel(AwesomeIcon.PLUS, "24"));
		hbox.getChildren().add(
				AwesomeDude.createIconLabel(AwesomeIcon.USER, "24"));
		addBuddy.setGraphic(hbox);
		list.setItems(ChatUtils.getBuddys());
		list.setManaged(true);
		list.setCellFactory(new Callback<ListView<Buddy>, ListCell<Buddy>>() {
			@Override
			public ListCell<Buddy> call(ListView<Buddy> listView) {
				return new BuddyCell();
			}
		});

		searchInput.textProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue<? extends String> observable, String oldVal,
					String newVal) {
				handleSearchByKey(oldVal, newVal);
			}

		});

		AwesomeDude.setIcon(faSearch, AwesomeIcon.SEARCH, "20");
		Platform.runLater(() -> searchInput.requestFocus());
	}

	ObservableList<String> entries = FXCollections.observableArrayList();

	public void handleSearchByKey(String oldVal, String newVal) {
		// If the number of characters in the text box is less than last time
		// it must be because the user pressed delete
		if (oldVal != null && (newVal.length() < oldVal.length())) {
			// Restore the lists original set of entries
			// and start from the beginning
			list.setItems(ChatUtils.getBuddys());
		}

		// Change to upper case so that case is not an issue
		newVal = newVal.toUpperCase();

		// Filter out the entries that don't contain the entered text
		ObservableList<Buddy> subentries = FXCollections.observableArrayList();
		for (Buddy entry : list.getItems()) {
			Buddy entryText = entry;
			if (entryText.toString().toUpperCase().contains(newVal)) {
				subentries.add(entryText);
			}
		}
		list.setItems(subentries);
		list.getSelectionModel().select(0);

	}

	@FXML
	private void startPalaverAction() throws VetoException, FlowException {
		Buddy buddy = list.getSelectionModel().getSelectedItems().get(0);
		if (buddy != null) {
			logger.fine("Starting palaver with " + buddy.getJid());
			// TODO FIX ME
			// try {
			// Storage.getById(Palaver.class, buddy.getAccount() + ":"
			// + StringUtils.parseBareAddress(buddy.getJid()));
			// logger.finer("Palaver does not exists");
			// } catch (IllegalArgumentException e) {
			// Palaver p = new Palaver();
			// p.setAccount(buddy.getAccount());
			// p.setRecipient(buddy.getJid());
			// Storage.getList(Palaver.class).add(p);
			// logger.finer(p.getAccount()
			// + " is starting palaver with " + p.getRecipient());
			// }
		}
		UiUtils.getFlowHandler(context).navigateBack();
	}
}
