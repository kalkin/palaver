package de.xsrc.palaver.controller;

import de.xsrc.palaver.beans.Palaver;
import de.xsrc.palaver.controls.HistoryControl;
import de.xsrc.palaver.controls.OpenPalaverList;
import de.xsrc.palaver.models.PalaverModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import org.datafx.controller.FXMLController;
import org.datafx.controller.flow.action.LinkAction;
import org.jivesoftware.smack.util.StringUtils;

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
	private BorderPane titlePane;

	@FXML
	private Button showOpenPalaverButton;

	@FXML
	private OpenPalaverList palaverListControl;
	private HashMap<Palaver, HistoryControl> historyMap = new HashMap<>();
	private PalaverModel model = PalaverModel.getInstance();

	@FXML
	private void initialize() {
		palaverListControl.selectedPalaver().addListener((observable, oldValue, newValue) -> {
			if (!historyMap.containsKey(newValue)) {
				try {

					HistoryControl history = new HistoryControl(newValue);
					historyMap.put(newValue, history);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			historyPane.setCenter(historyMap.get(newValue));

		});
		showOpenPalaverButton.visibleProperty().bind(palaverListControl.visibleProperty().not());
		showOpenPalaverButton.managedProperty().bind(palaverListControl.managedProperty().not());
		showOpenPalaverButton.cancelButtonProperty().bind(palaverListControl.visibleProperty().not());
		palaverListControl.visibleProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				Palaver p = palaverListControl.selectedPalaver().get();
				Text text = new Text(StringUtils.parseName(p.getRecipient()));
				text.getStyleClass().add("title");
				titlePane.setCenter(text);
				historyPane.setMaxWidth(1024);
			} else {
				Text text = new Text("Palavers");
				text.getStyleClass().add("title");
				titlePane.setCenter(text);
				historyPane.setMaxWidth(768);
			}
		});

	}

	@FXML
	private void showAction() {
		palaverListControl.hide(false);
	}
}
