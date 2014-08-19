package de.xsrc.palaver.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import org.datafx.controller.FXMLController;
import org.datafx.controller.flow.Flow;
import org.datafx.controller.flow.FlowException;
import org.datafx.controller.flow.action.LinkAction;

import de.xsrc.palaver.model.Palaver;

@FXMLController("/fxml/MainView.fxml")
public class MainController {

	@FXML
	@LinkAction(AccountController.class)
	private Button showAccountsButton;

	@FXML
	private ListView<Palaver> palaverList;

	@FXML
	private void initialize() {
		ObservableList<Palaver> palavers = Utils.getStorage(Palaver.class).getAll();
		palaverList.setItems(palavers);
	}

	@FXML
	private void addPalaver() {
		Flow f = new Flow(AddPalaverController.class);
		try {
			Utils.getDialog(f).show();
		} catch (FlowException e) {
			e.printStackTrace();
		}
	}

}
