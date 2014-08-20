package de.xsrc.palaver.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

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
	private ListView<String> list;

	@FXML
	private void initialize() {
		back.setGraphic(AwesomeDude.createIconLabel(AwesomeIcon.CHEVRON_LEFT));
		HBox hbox = new HBox();
		hbox.setAlignment(Pos.CENTER);
		hbox.getChildren().add(AwesomeDude.createIconLabel(AwesomeIcon.PLUS));
		hbox.getChildren().add(AwesomeDude.createIconLabel(AwesomeIcon.USER));
		addBuddy.setGraphic(hbox);
		ObservableList<String> buddyList = ChatUtils.getBuddys();
		list.setItems(buddyList);
	}

}
