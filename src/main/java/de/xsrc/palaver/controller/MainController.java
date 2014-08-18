package de.xsrc.palaver.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import org.datafx.controller.FXMLController;
import org.datafx.controller.flow.Flow;
import org.datafx.controller.flow.FlowException;
import org.datafx.controller.flow.action.LinkAction;

@FXMLController("../view/MainView.fxml")
public class MainController {

	@FXML
	@LinkAction(AccountController.class)
	private Button showAccountsButton;

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
