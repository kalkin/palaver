package de.xsrc.palaver.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import org.datafx.controller.FXMLController;
import org.datafx.controller.flow.Flow;
import org.datafx.controller.flow.FlowException;
import org.datafx.controller.flow.action.BackAction;

@FXMLController("/fxml/AccountView.fxml")
public class AccountController {
	@FXML
	@BackAction
	private Button back;

	@FXML
	//@ActionMethod(value = "addAccountAction")
	private void addAccount() {
		Flow f = new Flow(AddAccountController.class);
		try {
			Utils.getDialog(f).show();
		} catch (FlowException e) {
			e.printStackTrace();
		}

	}
}
