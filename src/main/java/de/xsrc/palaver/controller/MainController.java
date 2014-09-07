package de.xsrc.palaver.controller;

import de.xsrc.palaver.beans.Palaver;
import de.xsrc.palaver.controls.OpenPalaverList;
import de.xsrc.palaver.models.PalaverModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import org.datafx.controller.FXMLController;
import org.datafx.controller.flow.action.LinkAction;

import java.util.HashMap;
import java.util.logging.Logger;

@FXMLController("/fxml/MainView.fxml")
public class MainController {
	private static final Logger logger = Logger.getLogger(MainController.class.getName());

	@FXML
	@LinkAction(AccountController.class)
	private Button showAccountsButton;

	@FXML
	@LinkAction(ContactController.class)
	private Button showBuddyListButton;

	@FXML
	private BorderPane historyPane;

	@FXML
	private Button showOpenPalaverButton;

	@FXML
	private OpenPalaverList palaverListControl;
	private HashMap<Palaver, HistoryController> historyMap = new HashMap<>();
	private PalaverModel model = PalaverModel.getInstance();

	@FXML
	private void initialize() {
		palaverListControl.selectedPalaver().addListener((observable, oldValue, newValue) -> {
			if (!historyMap.containsKey(newValue)) {
				try {

					HistoryController history = new HistoryController(newValue);
					historyMap.put(newValue, history);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			historyPane.setCenter(historyMap.get(newValue));
//			historyMap.get(newValue).requestFocus();
		});
		showOpenPalaverButton.visibleProperty().bind(palaverListControl.visibleProperty().not());
		showOpenPalaverButton.managedProperty().bind(palaverListControl.managedProperty().not());
		showOpenPalaverButton.cancelButtonProperty().bind(palaverListControl.visibleProperty().not());
	}

	@FXML
	private void showAction() {
		palaverListControl.hide(false);
	}
}
