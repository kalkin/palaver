package de.xsrc.palaver.controller;

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
import org.datafx.controller.flow.action.BackAction;

import de.jensd.fx.fontawesome.AwesomeDude;
import de.jensd.fx.fontawesome.AwesomeIcon;
import de.xsrc.palaver.xmpp.ChatUtils;

@FXMLController("/fxml/BuddyListView.fxml")
public class BuddyListView {
	@FXML
	@BackAction
	private Button back;

	@FXML
	private Button addBuddy;

	@FXML
	private Label faSearch;

	@FXML
	private TextField searchInput;

	@FXML
	private ListView<String> list;

	@FXML
	private void initialize() {
		back.setGraphic(AwesomeDude.createIconLabel(AwesomeIcon.CHEVRON_LEFT));
		HBox hbox = new HBox();
		hbox.setAlignment(Pos.CENTER);
		hbox.getChildren().add(AwesomeDude.createIconLabel(AwesomeIcon.PLUS));
		hbox.getChildren().add(AwesomeDude.createIconLabel(AwesomeIcon.USER));
		addBuddy.setGraphic(hbox);
		list.setItems(ChatUtils.getBuddys());
		list.setManaged(true);
		list.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override
			public ListCell<String> call(ListView<String> listView) {
				return new BuddyCell();
			}
		});

		searchInput.textProperty().addListener(new ChangeListener<String>() {
			public void changed(ObservableValue observable, String oldVal,
					String newVal) {
				handleSearchByKey(oldVal, newVal);
			}
		});

		faSearch.setGraphic(AwesomeDude.createIconLabel(AwesomeIcon.SEARCH));
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
		ObservableList<String> subentries = FXCollections.observableArrayList();
		for (Object entry : list.getItems()) {
			String entryText = (String) entry;
			if (entryText.toUpperCase().contains(newVal)) {
				subentries.add(entryText);
			}
		}
		list.setItems(subentries);
		list.getSelectionModel().select(0);

	}
}
